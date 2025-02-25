'use client';

import React, { useState } from 'react';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Button, Dialog, DialogActions, DialogContent,
  DialogContentText, DialogTitle, CircularProgress,
} from '@mui/material';
import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';

interface BannedSeller {
  id: string;
  firstName: string;
  lastName: string;
  age: number;
  email: string;
  role: string;
  status: string;
}

interface TableBannedSellersProps {
  data: BannedSeller[];
}

const TableBannedSellers: React.FC<TableBannedSellersProps> = ({ data }) => {
  const [bannedSellers, setBannedSellers] = useState(data);
  const [unbanDialogOpen, setUnbanDialogOpen] = useState(false);
  const [processingUnban, setProcessingUnban] = useState(false);
  const [errorDialogOpen, setErrorDialogOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [selectedSeller, setSelectedSeller] = useState<BannedSeller | null>(null);
  const { data: session }: any = useSession();
  const router = useRouter()
  const handleUnbanClick = (seller: BannedSeller) => {
    setSelectedSeller(seller);
    setUnbanDialogOpen(true);
  };

  const handleUnbanConfirm = async () => {
    if (!selectedSeller) return;
    setProcessingUnban(true);
    try {
      const response = await fetch(
        `/api/proxy/api/seller/unban/${selectedSeller.id}`,
        {
          headers: {
            Authorization: `Bearer ${session?.accessToken}`,
            'Content-Type': 'application/json',
          },
          method: 'POST',
        }
      );

      if (!response.ok) {
        throw new Error(`Failed to unban the seller.`);
      }
      setBannedSellers(prevData => prevData.filter(item => item.id !== selectedSeller.id));
      router.refresh()
      setProcessingUnban(false);
      setUnbanDialogOpen(false);
    } catch (error: any) {
      console.error(`Erro ao desbanir o seller:`, error);
      setErrorMessage(error.message || 'An unexpected error occurred.');
      setErrorDialogOpen(true);
      setProcessingUnban(false);
      setUnbanDialogOpen(false);
    }
  };

  return (
    <div>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {bannedSellers.map((seller) => (
              <TableRow key={seller.id}>
                <TableCell>{seller.id}</TableCell>
                <TableCell>{`${seller.firstName} ${seller.lastName}`}</TableCell>
                <TableCell>{seller.email}</TableCell>
                <TableCell>
                  <Button
                    variant="contained"
                    color="success"
                    onClick={() => handleUnbanClick(seller)}
                  >
                    Unban
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <Dialog open={unbanDialogOpen} onClose={() => setUnbanDialogOpen(false)}>
          <DialogTitle>Unban Seller</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Are you sure you want to unban {`${selectedSeller?.firstName} ${selectedSeller?.lastName}`}?
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setUnbanDialogOpen(false)} color="secondary">
              Cancel
            </Button>
            <Button onClick={handleUnbanConfirm} color="primary" disabled={processingUnban}>
              {processingUnban ? <CircularProgress size={24} /> : 'Confirm'}
            </Button>
          </DialogActions>
        </Dialog>
      </TableContainer>

      {/* Diálogo de Erro */}
      <Dialog open={errorDialogOpen} onClose={() => setErrorDialogOpen(false)}>
        <DialogTitle>Error</DialogTitle>
        <DialogContent>
          <DialogContentText>{errorMessage}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setErrorDialogOpen(false)} color="primary">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default TableBannedSellers;

'use client';

import React, { useState } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  CircularProgress,
} from '@mui/material';
import { useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';

interface ApprovedSeller {
  id: string;
  firstName: string;
  lastName: string;
  age: number;
  email: string;
  role: string;
  status: string;
}

interface TableApprovedSellersProps {
  data: ApprovedSeller[];
}

const TableApprovedSellers: React.FC<TableApprovedSellersProps> = ({ data }) => {
  const [approvedSellers, setApprovedSellers] = useState(data);
  const [banDialogOpen, setBanDialogOpen] = useState(false);
  const [revokeDialogOpen, setRevokeDialogOpen] = useState(false);
  const [processingAction, setProcessingAction] = useState(false);
  const [selectedSeller, setSelectedSeller] = useState<ApprovedSeller | null>(null);
  const [errorDialogOpen, setErrorDialogOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const { data: session }: any = useSession();
  const router = useRouter();

  const handleBanClick = (seller: ApprovedSeller) => {
    setSelectedSeller(seller);
    setBanDialogOpen(true);
  };

  const handleRevokeClick = (seller: ApprovedSeller) => {
    setSelectedSeller(seller);
    setRevokeDialogOpen(true);
  };

  const handleBanConfirm = async () => {
    if (!selectedSeller) return;
    setProcessingAction(true);

    try {
      const response = await fetch(`/api/proxy/api/seller/ban/${selectedSeller.id}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${session?.accessToken}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to ban the user.');
      }

      setApprovedSellers(prevSellers =>
        prevSellers.filter(seller => seller.id !== selectedSeller.id)
      );
      router.refresh();
      setProcessingAction(false);
      setBanDialogOpen(false);
      setSelectedSeller(null);
    } catch (error: any) {
      console.error('Erro ao banir o usuário:', error);
      setErrorMessage(error.message || 'An unexpected error occurred.');
      setErrorDialogOpen(true);
      setProcessingAction(false);
      setBanDialogOpen(false);
    }
  };

  const handleRevokeConfirm = async () => {
    if (!selectedSeller) return;
    setProcessingAction(true);

    try {
      const response = await fetch(`/api/proxy/api/seller/revoke/${selectedSeller.id}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${session?.accessToken}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to revoke seller status.');
      }

      setApprovedSellers(prevSellers =>
        prevSellers.filter(seller => seller.id !== selectedSeller.id)
      );
      router.refresh();
      setProcessingAction(false);
      setRevokeDialogOpen(false);
      setSelectedSeller(null);
    } catch (error: any) {
      console.error('Erro ao revogar status de vendedor:', error);
      setErrorMessage(error.message || 'An unexpected error occurred.');
      setErrorDialogOpen(true);
      setProcessingAction(false);
      setRevokeDialogOpen(false);
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
              <TableCell>Status</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {approvedSellers.map(seller => (
              <TableRow key={seller.id}>
                <TableCell>{seller.id}</TableCell>
                <TableCell>{`${seller.firstName} ${seller.lastName}`}</TableCell>
                <TableCell>{seller.email}</TableCell>
                <TableCell>ATIVO</TableCell>
                <TableCell>
                  <Button
                    variant="contained"
                    color="error"
                    onClick={() => handleBanClick(seller)}
                  >
                    Ban
                  </Button>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={() => handleRevokeClick(seller)}
                    style={{ marginLeft: '8px' }}
                  >
                    Revoke
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Ban Dialog */}
      <Dialog open={banDialogOpen} onClose={() => setBanDialogOpen(false)}>
        <DialogTitle>Ban Seller</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to ban{' '}
            {selectedSeller &&
              `${selectedSeller.firstName} ${selectedSeller.lastName}`}?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setBanDialogOpen(false)} color="primary">
            Cancel
          </Button>
          <Button onClick={handleBanConfirm} color="error" disabled={processingAction}>
            {processingAction ? <CircularProgress size={24} /> : 'Confirm'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Revoke Dialog */}
      <Dialog open={revokeDialogOpen} onClose={() => setRevokeDialogOpen(false)}>
        <DialogTitle>Revoke Seller Status</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to revoke the seller status of{' '}
            {selectedSeller &&
              `${selectedSeller.firstName} ${selectedSeller.lastName}`}?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRevokeDialogOpen(false)} color="primary">
            Cancel
          </Button>
          <Button onClick={handleRevokeConfirm} color="primary" disabled={processingAction}>
            {processingAction ? <CircularProgress size={24} /> : 'Confirm'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Error Dialog */}
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

export default TableApprovedSellers;

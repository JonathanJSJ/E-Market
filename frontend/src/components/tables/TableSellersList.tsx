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

interface SellerRequest {
  id: string;
  user: {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    role: string;
    status: string;
  };
  applicationStatus: string;
  createdAt: string;
}

interface TableSellersListProps {
  data: SellerRequest[];
}

const TableSellersList: React.FC<TableSellersListProps> = ({ data }) => {
  const [sellerList, setSellerList] = useState(data);
  const [approveDialogOpen, setApproveDialogOpen] = useState(false);
  const [rejectDialogOpen, setRejectDialogOpen] = useState(false);
  const [errorDialogOpen, setErrorDialogOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [processingApproval, setProcessingApproval] = useState(false);
  const [selectedRequest, setSelectedRequest] = useState<SellerRequest | null>(null);
  const { data: session }: any = useSession();
  const router = useRouter();

  const handleApproveClick = (request: SellerRequest) => {
    setSelectedRequest(request);
    setApproveDialogOpen(true);
  };

  const handleRejectClick = (request: SellerRequest) => {
    setSelectedRequest(request);
    setRejectDialogOpen(true);
  };

  const handleActionConfirm = async (approve: boolean) => {
    if (!selectedRequest) return;

    setProcessingApproval(true);

    try {
      const response = await fetch(
        `/api/proxy/api/seller-applications/${selectedRequest.id}?approve=${approve}`,
        {
          headers: {
            Authorization: `Bearer ${session?.accessToken}`,
            'Content-Type': 'application/json',
          },
          method: 'PUT',
        }
      );

      if (!response.ok) {
        throw new Error(`Failed to ${approve ? 'approve' : 'reject'} the seller.`);
      }
      setSellerList(prevData => prevData.filter(item => item.id !== selectedRequest.id));
      router.refresh()
      setProcessingApproval(false);
      setApproveDialogOpen(false);
      setRejectDialogOpen(false);
    } catch (error: any) {
      console.error(`Erro ao ${approve ? 'aprovar' : 'reprovar'} o seller:`, error);
      setErrorMessage(error.message || 'An unexpected error occurred.');
      setErrorDialogOpen(true);
      setProcessingApproval(false);
      setApproveDialogOpen(false);
      setRejectDialogOpen(false);
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
              <TableCell>Date of Registration</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {sellerList.map(seller => (
              <TableRow key={seller.id}>
                <TableCell>{seller.id}</TableCell>
                <TableCell>{`${seller.user.firstName} ${seller.user.lastName}`}</TableCell>
                <TableCell>{seller.user.email}</TableCell>
                <TableCell>{new Date(seller.createdAt).toLocaleDateString()}</TableCell>
                <TableCell>{seller.applicationStatus}</TableCell>
                <TableCell>
                  <Button
                    variant="contained"
                    color="success"
                    onClick={() => handleApproveClick(seller)}
                  >
                    Approve
                  </Button>
                  <Button
                    variant="contained"
                    color="error"
                    onClick={() => handleRejectClick(seller)}
                    style={{ marginLeft: '8px' }}
                  >
                    Reject
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Diálogo de Confirmação de Aprovação */}
      <Dialog open={approveDialogOpen} onClose={() => setApproveDialogOpen(false)}>
        <DialogTitle>Approve Seller</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to approve{' '}
            {selectedRequest &&
              `${selectedRequest.user.firstName} ${selectedRequest.user.lastName}`}{' '}
            as a seller?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setApproveDialogOpen(false)} color="secondary">
            Cancel
          </Button>
          <Button
            onClick={() => handleActionConfirm(true)}
            color="primary"
            disabled={processingApproval}
          >
            {processingApproval ? <CircularProgress size={24} /> : 'Confirm'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Diálogo de Confirmação de Rejeição */}
      <Dialog open={rejectDialogOpen} onClose={() => setRejectDialogOpen(false)}>
        <DialogTitle>Reject Seller</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to reject{' '}
            {selectedRequest &&
              `${selectedRequest.user.firstName} ${selectedRequest.user.lastName}`}{' '}
            as a seller?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRejectDialogOpen(false)} color="secondary">
            Cancel
          </Button>
          <Button
            onClick={() => handleActionConfirm(false)}
            color="error"
            disabled={processingApproval}
          >
            {processingApproval ? <CircularProgress size={24} /> : 'Confirm'}
          </Button>
        </DialogActions>
      </Dialog>

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

export default TableSellersList;

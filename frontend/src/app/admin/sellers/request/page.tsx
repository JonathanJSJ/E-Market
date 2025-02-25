import React from 'react';
import TableSellersList from '@/components/tables/TableSellersList';
import { fetchServer } from '@/services/fetchServer';
import { redirect } from 'next/navigation';

export default async function AdminPage() {

  const response = await fetchServer('/api/seller-applications')
  const sellers = await response.json()

  if (!sellers.content && response.status == 403) {
    redirect('/login')
  }

  const listSellersApplications = sellers.content.filter((sell: { applicationStatus: string; }) => sell.applicationStatus === 'PENDING')

  return (
    <div style={{ padding: '20px' }}>
      <h1>Applied sellers</h1>
      <TableSellersList data={listSellersApplications} />
    </div>
  );
};


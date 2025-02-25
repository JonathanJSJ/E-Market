import React from 'react';
import TableBannedSellers from '@/components/tables/TableBannedSeller';
import { fetchServer } from '@/services/fetchServer';
import { redirect } from 'next/navigation';

export default async function BannedSellersPage() {

  const response = await fetchServer('/api/seller/banned')
  const sellers = await response.json()

  if (!sellers.content && response.status == 403) {
    redirect('/login')
  }

  const listSellersBanned = sellers?.content

  if (!listSellersBanned) {
    return (
      <div>
        <h3>Backend error</h3>
      </div>
    )

  }

  return (
    <div style={{ padding: '20px' }}>
      <h1>Banned sellers</h1>
      <TableBannedSellers data={listSellersBanned} />
    </div>
  );
};

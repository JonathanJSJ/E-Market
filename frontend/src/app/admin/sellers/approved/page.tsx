import React from 'react';
import TableApprovedSellers from '@/components/tables/TableApprovedSellers';
import { fetchServer } from '@/services/fetchServer';
import { redirect } from 'next/navigation';

export default async function ApprovedSellersPage() {

  const response = await fetchServer('/api/seller/active', {
    method: "GET",
  })
  const sellers = await response.json()

  if (!sellers.content && response.status == 403) {
    redirect('/login')
  }

  const listSellersApproved = sellers?.content

  if (!listSellersApproved) {
    return (
      <div>
        <h3>Backend error</h3>
      </div>
    )

  }
  return (
    <div style={{ padding: '20px' }}>
      <h1>Approved sellers</h1>
      <TableApprovedSellers data={listSellersApproved} />
    </div>
  );
};


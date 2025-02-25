import NavBarAdmin from '@/components/navbarAdmin/NavBarAdmin';
import { fetchServer } from '@/services/fetchServer';
import { Box } from '@mui/system';
import React from 'react';
import { redirect } from 'next/navigation';

export default async function AdminPage({
  children,
}: {
  children: React.ReactElement;
}) {
  const sellers = await fetchServer('/api/user/application').then((res) => res.json())

  if (!sellers) {
    redirect('/404');
  }

  return (
    <Box>
      <NavBarAdmin />
      <Box sx={{ padding: '20px' }}>
        {children}
      </Box>
    </Box>
  );
}

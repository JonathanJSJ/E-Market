'use client';

import React, { useEffect, useState } from 'react';
import { Tabs, Tab } from '@mui/material';
import { usePathname, useRouter } from 'next/navigation';

const NavBarAdmin: React.FC = () => {
  const router = useRouter();
  const [tabIndex, setTabIndex] = useState(0);
  const pathname = usePathname();

  useEffect(() => {
    if (pathname.includes('/admin/sellers/request')) {
      setTabIndex(0);
    } else if (pathname.includes('/admin/sellers/approved')) {
      setTabIndex(1);
    } else if (pathname.includes('/admin/sellers/banned')) {
      setTabIndex(2);
    }
  }, [pathname]);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabIndex(newValue);
    if (newValue === 0) {
      router.push('/admin/sellers/request');
    } else if (newValue === 1) {
      router.push('/admin/sellers/approved');
    } else {
      router.push('/admin/sellers/banned');
    }
  };

  return (
    <Tabs value={tabIndex} onChange={handleTabChange} centered>
      <Tab label="Requests for approval" />
      <Tab label="Approved sellers" />
      <Tab label="Banned sellers" />
    </Tabs>
  );
};

export default NavBarAdmin;

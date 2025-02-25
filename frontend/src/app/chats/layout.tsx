'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useSession } from 'next-auth/react';
import { Box, Button, List, ListItem, ListItemText, Typography } from '@mui/material';
import { useUserProfile } from '@/hooks/useProfile';

export default function ChatsLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [chats, setChats] = useState<any[]>([]);
  const { data: session }: any = useSession();
  const { profile, isLoading } = useUserProfile()

  useEffect(() => {
    const fetchChats = async () => {
      if (!session?.accessToken) return

      const response = await fetch('/api/proxy/api/chats', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${session?.accessToken}`,
          'Content-Type': 'application/json',
        },
      });

      const data = await response.json();

      setChats(data);
    };

    fetchChats();
  }, [session]);

  return (
    <Box display="flex" height="100vh">
      {/* Sidebar */}
      <Box
        sx={{
          width: '30%',
          borderRight: '1px solid #ccc',
          padding: 2,
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <Typography variant="h3">Chats</Typography>
          <Button variant="outlined" href="/" size="large">
            Back
          </Button>
        </Box>

        <List>
          {chats &&
            chats.map((chat: any) => (
              <Link
                key={chat.id}
                href={`/chats/${chat.id}`}
                style={{ textDecoration: 'none', color: 'inherit' }}
              >
                <ListItem
                  sx={{
                    cursor: 'pointer',
                    backgroundColor: '#fff',
                    borderBottom: '1px solid #eee',
                    padding: 1.5,
                    '&:hover': {
                      backgroundColor: '#f9f9f9',
                    },
                  }}
                >
                  <ListItemText
                    primary={<Typography variant="subtitle1">{isLoading ? '' : profile?.id === chat.seller.id ? chat.user.name : chat.seller.name}</Typography>}
                    secondary={
                      chat.messages.length
                        ? chat.messages[chat.messages.length - 1]?.message
                        : 'Sem mensagens'
                    }
                  />
                </ListItem>
              </Link>
            ))}
        </List>
      </Box>

      {/* Conteúdo principal */}
      <Box sx={{ flex: 1, padding: 2 }}>{children}</Box>
    </Box>
  );
}

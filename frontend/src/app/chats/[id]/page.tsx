'use client';

import { useState } from 'react';
import { useParams } from 'next/navigation';
import { useMessages } from '@/hooks/useMessages';
import { useUserProfile } from '@/hooks/useProfile';
import { useSession } from 'next-auth/react';
import { Box, Button, List, ListItem, ListItemText, Paper, TextField, Typography } from '@mui/material';

export default function ChatPage() {
  const { id } = useParams();
  const { profile } = useUserProfile()
  const { data: session }: any = useSession()
  const { selectedChatHook, sendMessage } = useMessages(id as string | number, session?.accessToken, profile?.firstName); // Hook para gerenciar mensagens
  const [newMessage, setNewMessage] = useState('');

  const handleSendMessage = () => {
    if (newMessage) {
      sendMessage(newMessage, id as string, profile?.id, session.accessToken);
      setNewMessage('');
    }
  };


  return (
    <Box>

      {/* Título do chat */}
      <Typography variant="h3" gutterBottom>
        Chat Ticket ID: {id}
      </Typography>

      {/* Área de mensagens */}
      <Paper
        elevation={3}
        sx={{
          height: '70%',
          overflowY: 'auto',
          marginBottom: 2,
          padding: 2,
        }}
      >
        <List>
          {selectedChatHook?.messages?.map((msg: any, index: number) => (
            <ListItem key={index} sx={{ paddingLeft: 0 }}>
              <ListItemText
                primary={<strong>{msg.user.name}:</strong>}
                secondary={msg.message}
              />
            </ListItem>
          ))}
        </List>
      </Paper>

      {/* Área de entrada de mensagem */}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <TextField
          fullWidth
          variant="outlined"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Digite sua mensagem"
        />
        <Button variant="contained" onClick={handleSendMessage}>
          Submit
        </Button>
      </Box>
    </Box>
  );
}

'use client';
import { useState, useEffect } from 'react';
import { io, Socket } from 'socket.io-client';
import { createMessage, getAllMessages } from '@/services/messageService';
import { useSession } from 'next-auth/react';

export const useMessages = (
  id: number | string | null,
  accessToken: string | null,
  name?: string | null
) => {
  const [selectedChatHook, setSelectedChatHook] = useState<any>({});
  const [socket, setSocket] = useState<Socket | null>(null);

  useEffect(() => {
    if (!id && !selectedChatHook) return;

    const fetchMessages = async () => {
      try {
        const messagesFromApi = await getAllMessages(
          id as string | number,
          accessToken
        );

        setSelectedChatHook(messagesFromApi);
      } catch (error) {
        console.error('Erro ao buscar mensagens:', error);
      }
    };

    fetchMessages();

    const newSocket = io();
    setSocket(newSocket);

    newSocket.on('connect', () => {
      console.log('Conectado ao servidor de WebSocket');
    });

    newSocket.on('message', (chatObjectIO) => {
      if (id === chatObjectIO.id) {
        setSelectedChatHook(chatObjectIO);
      }
    });

    return () => {
      newSocket.disconnect();
    };
  }, [id]);

  const sendMessage = async (
    newMessage: string,
    id: string,
    userId?: string,
    accessToken?: string
  ) => {
    if (!newMessage.trim()) return;

    try {
      const chatObjectResponse = await fetch(
        `/api/proxy/api/chats/${id}/messages`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${accessToken}`,
          },
          body: JSON.stringify({ message: newMessage, user: userId }),
        }
      );

      selectedChatHook.messages = [
        ...selectedChatHook.messages,
        { message: newMessage, user: { id: userId, name: name } },
      ];

      setSelectedChatHook(selectedChatHook);

      socket?.emit('message', selectedChatHook);
    } catch (error) {
      console.error('Erro ao enviar mensagem:', error);
    }
  };

  return { selectedChatHook, sendMessage };
};

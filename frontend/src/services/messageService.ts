import { MessageWithUser } from '@/utils/types';

const API_URL = '/api/messages';

export const createMessage = async (chatObject: any): Promise<any> => {
  try {
    const response = await fetch(`${API_URL}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(chatObject),
    });
    if (!response.ok) {
      throw new Error('Erro ao enviar mensagem');
    }

    const newMessage = await response.json();

    return newMessage;
  } catch (error) {
    console.error('Erro ao enviar mensagem:', error);
    throw error;
  }
};

export const getAllMessages = async (
  chatId: string | number,
  accessToken: string | null
): Promise<MessageWithUser[]> => {
  try {
    const response = await fetch(`/api/proxy/api/chats/` + chatId, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      throw new Error('Erro ao buscar mensagens');
    }
    return await response.json();
  } catch (error) {
    console.error('Erro ao buscar grupos:', error);
    throw error;
  }
};

import io from 'socket.io-client';

export async function handleSendMessageSocketIo(message: string) {
  const socket = io();

  socket.emit('clientMessage', message);

  try {
    const response = await fetch('/api/messages', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        content: message,
      }),
    });

    if (!response.ok) {
      throw new Error('Erro ao salvar mensagem no banco de dados.');
    }
  } catch (error) {
    console.error('Erro ao fazer o fetch:', error);
  }
}

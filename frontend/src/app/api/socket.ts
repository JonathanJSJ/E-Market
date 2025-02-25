import { Server } from 'socket.io';

export default function handler(req: any, res: any) {
  if (!res.socket.server.io) {
    const io = new Server(res.socket.server, {
      path: '/api/socket',
      cors: { origin: '*' },
    });

    res.socket.server.io = io;

    io.on('connection', (socket) => {
      console.log('Usuário conectado:', socket.id);

      socket.on('sendMessage', (data) => {
        console.log('Nova mensagem recebida:', data);
        io.emit('message', data);
      });

      socket.on('disconnect', () => {
        console.log('Usuário desconectado:', socket.id);
      });
    });
  }
  res.end();
}

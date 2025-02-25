export interface GroupWithMessage {
  name: string;
  id: string;
  createdAt: Date;
  updatedAt: Date;
  lastMessage: string;
}

export interface MessageWithUser {
  id: string;
  content: string;
  createdAt: Date;
  userId: string;
  groupId: string;
  email: string;
  user: {
    name: string;
    email: string;
    image: string;
  };
}

export interface UseMessagesReturn {
  messages: MessageWithUser[];
  sendMessage: (message: string, groupId: string) => void;
  input: string;
  setInput: (input: string) => void;
}

export interface SendMessagePayload {
  groupId: string;
  message: MessageWithUser;
  userId: string;
}

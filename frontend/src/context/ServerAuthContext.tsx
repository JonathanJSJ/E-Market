import { headers } from 'next/headers';
import ClientAuthProvider from './ClientAuthContext';

export type User = {
  IDUSUARIO: string | number;
  LOGIN: string;
};

type ServerAuthProviderProps = {
  children: React.ReactNode;
};

export default async function ServerAuthProvider({ children }: ServerAuthProviderProps) {
  const headersList = headers();
  const userInfoHeader = headersList.get('x-user-info') as string;

  let user: User = JSON.parse(userInfoHeader);
  return <ClientAuthProvider user={user}>{children}</ClientAuthProvider>;
}
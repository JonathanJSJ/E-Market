'use client';

import { createContext, useState, ReactNode } from 'react';
import { User } from './ServerAuthContext';
import { destroyCookie, setCookie } from 'nookies';
import { useRouter } from 'next/navigation';
import { fetchClient } from '@/services/fetchClient';
import { recoverUserInformation } from '@/services/auth';
import { SessionProvider } from 'next-auth/react';

type ClientAuthContextType = {
  user: User;
  signIn: (username: string, password: string) => Promise<any>;
  logout: () => void;
};

export const AuthContext = createContext<ClientAuthContextType>({} as ClientAuthContextType);

type ClientAuthProviderProps = {
  children: ReactNode;
  user: User;
};

export default function ClientAuthProvider({ children, user }: ClientAuthProviderProps) {
  const [currentUser, setCurrentUser] = useState<User>(user);
  const router = useRouter();

  async function signIn(username: string, password: string) {
    try {
      const response = await fetchClient('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password }),
        headers: { 'Content-Type': 'application/json' },
      });

      if (response.ok) {
        const data = await response.json();
        setCookie(undefined, 'nextauth.token.ds4', data.token, {
          maxAge: 60 * 60 * 8,
          path: '/',
        });

        const newUser = await recoverUserInformation()
        if (newUser) {
          setCurrentUser(newUser);
          return data.token;
        }
      }

      return null;
    } catch (error) {
      console.error('Erro ao fazer login:', error);
      return null;
    }
  }

  function logout() {
    const nameCookie = 'nextauth.token.ds4'

    destroyCookie(undefined, nameCookie);
    document.cookie = `${nameCookie}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;

    window.location.reload();
  }

  return (
    <AuthContext.Provider value={{ user: currentUser, signIn, logout }}>
      <SessionProvider>
        {children}
      </SessionProvider>
    </AuthContext.Provider>
  );
}
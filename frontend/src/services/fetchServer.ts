'use server';

import { authOptions } from '@/app/api/auth/[...nextauth]/route';
import { getServerSession, Session } from 'next-auth';

export const fetchServer = async (
  input: Request | string | URL,
  init?: RequestInit | undefined
) => {
  const host = process.env.BACKEND_HOST;

  const session: (Session & { accessToken: string }) | null =
    await getServerSession(authOptions);

  const token: string | undefined = session?.accessToken;

  try {
    const response = await fetch(`${host}${input}`, {
      ...init,
      headers: {
        ...init?.headers,
        ...(token && { Authorization: `Bearer ${token}` }),
      },
    });

    return response;
  } catch (error) {
    console.error('Erro na requisição ao backend:', error);
    throw error;
  }
};

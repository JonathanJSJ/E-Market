'use client';
import { useSession } from 'next-auth/react';

export const fetchClient = async (
  input: Request | string | URL,
  init?: RequestInit | undefined
) => {
  const { data: session } = useSession();
  const token = session?.accessToken;

  const response = await fetch(input, {
    ...init,
    headers: {
      ...init?.headers,
      ...(token && { Authorization: `Bearer ${token}` }),
    },
  });

  return response;
};

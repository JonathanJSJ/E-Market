import { useState, useEffect } from 'react';
import { useSession } from 'next-auth/react';

export interface UserProfile {
  id: string;
  firstName: string;
  lastName: string;
  age: string;
  email: string;
  role: string;
  status: string;
}

export const useUserProfile = () => {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const { data: session }: any = useSession();

  useEffect(() => {
    if (!session?.accessToken) return;

    const fetchUserProfile = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const response = await fetch('/api/proxy/api/user/profile', {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${session.accessToken}`,
            'Content-Type': 'application/json',
          },
        });

        if (!response.ok) {
          throw new Error('Erro ao buscar o perfil');
        }

        const res = await response.json();
        setProfile(res);
      } catch (err: any) {
        setError(err.message || 'Erro ao buscar o perfil');
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserProfile();
  }, [session?.accessToken]);

  return {
    profile,
    isLoading,
    error,
  };
};

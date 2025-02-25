import { SignInRequestData } from '@/types/login';
import { fetchClient } from './fetchClient';
import 'dotenv/config';

export async function signInRequest(data: SignInRequestData) {
  try {
    const response = await fetchClient('/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: data.email,
        password: data.password,
      }),
    });

    if (!response.ok) {
      if (response.status === 500) {
        window.alert('Erro no servidor: 500');
      } else if (response.status === 401 || response.status === 404) {
        window.alert('Usuário ou senha inválidos');
      }
      return null;
    }

    return await response.json();
  } catch (error) {
    console.error('Erro ao fazer login:', error);
    return null;
  }
}

export async function recoverUserInformation() {
  try {
    const response = await fetchClient('/user/profile', {
      method: 'POST',
    });

    const user: any | null = await response.json();

    return user;
  } catch (error) {
    console.error('Erro ao recuperar informações do usuário:', error);
    return null;
  }
}

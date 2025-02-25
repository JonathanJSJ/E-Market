import NextAuth, { AuthOptions, Session, User } from 'next-auth';
import CredentialsProvider from 'next-auth/providers/credentials';
import { fetchServer } from '@/services/fetchServer';
import GoogleProvider from 'next-auth/providers/google';
import { JWT } from 'next-auth/jwt';
import jwt from 'jsonwebtoken';

export const authOptions: AuthOptions = {
  providers: [
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID as string,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET as string,
    }),
    CredentialsProvider({
      name: 'Credentials',
      credentials: {
        email: { label: 'Email', type: 'text', placeholder: 'you@example.com' },
        password: { label: 'Password', type: 'password' },
      },
      async authorize(credentials) {
        const email = credentials?.email;
        const password = credentials?.password;

        const response = await fetchServer('/api/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ email, password }),
        });

        if (response.status !== 200) {
          throw new Error('Falha ao fazer login');
        }

        const { token } = await response.json();

        if (token) {
          const profileResponse = await fetchServer('/api/user/profile', {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
            method: 'GET',
          });

          const profile = await profileResponse.json();

          if (profile) {
            return {
              token,
              id: profile.id || 1,
              email: profile.email,
              name: `${profile.firstName} ${profile.lastName}`,
            };
          }
        }

        return null;
      },
    }),
  ],
  pages: {
    signIn: '/login',
  },
  callbacks: {
    async signIn({ user, account, profile }: any) {
      if (account?.provider === 'google') {
        try {
          const bodyrequest = {
            email: profile?.email,
            token: account?.id_token,
          };

          const response = await fetchServer('/api/auth/login-social/google', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(bodyrequest),
          });
          const res = await response.json();
          if (!res?.token) {
            console.error('Erro ao logar/cadastrar no backend');
            return false;
          }
          user.token = res.token;
          return true;
        } catch (error) {
          console.error('Erro ao conectar ao backend:', error);
          return false;
        }
      }
      return true;
    },

    async jwt({ token, user }: any) {
      if (user) {
        token.accessToken = user.token;
        token.id = user.id;
        token.email = user.email;
        token.name = user.name;
      }

      if (isTokenExpired(token.accessToken)) {
        return {};
      }
      return token;
    },
    async session({ session, token }: { session: Session | any; token: JWT }) {
      if (!token.accessToken) {
        return null;
      }

      session.accessToken = token.accessToken;
      session.user = {
        id: token.id,
        email: token.email,
        name: token.name,
      };
      return session;
    },
  },
};

const handler = NextAuth(authOptions);

export { handler as GET, handler as POST };

function isTokenExpired(token: string): boolean {
  try {
    const decoded = jwt.decode(token) as { exp?: number };

    if (!decoded || !decoded.exp) {
      throw new Error('Invalid token or no exp field.');
    }

    const now = Math.floor(Date.now() / 1000);

    return now > decoded.exp;
  } catch (error) {
    console.error('Erro ao verificar token:', error);
    return true;
  }
}

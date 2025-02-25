import * as React from 'react';
import Head from 'next/head';
import './colors.css';
import ServerAuthProvider from '@/context/ServerAuthContext';
import './global.css'
import ThemeRegistry from '../utils/layout/ThemeRegistry';
import { ToastContainer } from 'react-toastify';

const Snowflakes = () => {
  const flakes = Array.from({ length: 80 }).map((_, index) => {
    const snowX = Math.random();
    const duration = Math.random() * 10 + 5;
    const delay = Math.random() * -10;

    return (
      <div
        key={index}
        className="snowflake"
        style={{
          left: `${snowX * 100}vw`,
          animationDuration: `${duration}s`,
          animationDelay: `${delay}s`,
          '--snow-x': snowX,
        }}
      />
    );
  });

  return <>{flakes}</>;
};

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR">
      <Head>
        <meta name="viewport" content="initial-scale=1, width=device-width" />
        <meta name="theme-color" content="var(--gray-07)" />
        <link
          rel="stylesheet"
          href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap"
        />
      </Head>
      <ServerAuthProvider>
        <body>
          <Snowflakes />
          <ThemeRegistry>{children}</ThemeRegistry>
          <ToastContainer />
        </body>
      </ServerAuthProvider>
    </html>
  );
}
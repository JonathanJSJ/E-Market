'use client'

import React, { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Image from "next/image";
import Inputemail from "@/components/login/email";
import InputPassword from "@/components/login/password";
import ButtonLogin from "@/components/login/buttons/login";
import { signIn, useSession } from "next-auth/react";
import styles from './LoginPage.module.css';
import ButtonRegister from "@/components/login/buttons/register";

export default function Login() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const redirectRoute = searchParams?.get('redirect') || '/start';
  const { data: session, status } = useSession();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loadingRequest, setLoadingRequest] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleemailChange = (value: string) => {
    setEmail(value);
  };

  const handlePasswordChange = (value: string) => {
    setPassword(value);
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!email || !password) return;
    setLoadingRequest(true);

    try {
      const result = await signIn('credentials', {
        redirect: false,
        email,
        password,
      });

      if (result?.error) {
        setErrorMessage('Usuário ou senha inválidos');
        setLoadingRequest(false);
      } else {
        router.replace(redirectRoute);
      }
    } catch (error) {
      setErrorMessage('Erro inesperado, tente novamente');
      setLoadingRequest(false);
    }
  };
  useEffect(() => {
    if (status === 'authenticated' && session) {

      router.replace(redirectRoute);
    }
  }, [status, session]);
  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.containerHeader}>
          <Image className={styles.logo} src='https://imgur.com/kDgIfIX.png'
            alt='Imagem'
            width={240}
            height={80} />
        </div>
        <h2 className={styles.title}>LOGIN</h2>
        <form onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <Inputemail onChange={handleemailChange} label='Email' email={email} />
          </div>
          <div className={styles.formGroup}>
            <InputPassword onChange={handlePasswordChange} label='Password' password={password} />
          </div>
          {errorMessage && <div className={styles.error}>{errorMessage}</div>}
          <div className={styles.loginButtons}>
            <ButtonLogin label='Login' disabledLoading={loadingRequest} type='submit' className={styles.buttonLogin} />
            <ButtonLogin label='Login with Google' disabledLoading={loadingRequest} className={styles.buttonLogin} onClick={() => signIn("google")} />
            <ButtonRegister label='Register' disabledLoading={loadingRequest} onClick={() => router.replace('/register')} className={styles.buttonLogin} />
          </div>
        </form>
      </div>
    </div>
  );
}

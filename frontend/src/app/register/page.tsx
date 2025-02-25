'use client'

import React, { useState } from "react";
import Image from "next/image";
import Inputemail from "@/components/login/email";
import InputPassword from "@/components/login/password";
import ButtonLogin from "@/components/login/buttons/login";
import styles from './RegisterPage.module.css';
import InputUsername from "@/components/login/username";
import InputAge from "@/components/login/age";
import { useRouter } from "next/navigation";
import ButtonRegister from "@/components/login/buttons/register";

export default function Register() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [age, setAge] = useState('');
  const router = useRouter();
  const [message, setMessage] = useState('');
  const [loadingRequest, setLoadingRequest] = useState(false);

  const handleemailChange = (value: string) => {
    setEmail(value);
  };

  const handleFirstNameChange = (value: string) => {
    setFirstName(value);
  };

  const handleLastNameChange = (value: string) => {
    setLastName(value);
  };

  const handleAgeChange = (value: string) => {
    setAge(value);
  };

  const handlePasswordChange = (value: string) => {
    setPassword(value);
  };

  const handleConfirmPasswordChange = (value: string) => {
    setConfirmPassword(value);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      setMessage('Passwords do not match');
      return;
    }

    if (password.length < 8) {
      setMessage('Very small password');
      return
    }
    setLoadingRequest(true)
    try {
      const response = await fetch('/api/proxyRegister', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          firstName: firstName,
          email: email,
          password: password,
          lastName: lastName,
          age: age,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        setMessage(data.message);
        router.replace('/login')
      } else {
        setMessage(data.error || 'An error occurred');
        setLoadingRequest(false)
      }
    } catch (error) {
      console.error('Erro:', error);
      setLoadingRequest(false)
      setMessage('An error occurred');
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.containerHeader}>
          <Image className={styles.logo} src='https://imgur.com/kDgIfIX.png'
            alt='Imagem'
            width={240}
            height={80} />
        </div>
        <h2 className={styles.title}>REGISTER</h2>

        <form onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <Inputemail required onChange={handleemailChange} label='Email' email={email}/>
          </div>
          <div className={styles.formGroup}>
            <InputUsername required onChange={handleFirstNameChange} label='FirstName' username={firstName}/>
          </div>
          <div className={styles.formGroup}>
            <InputUsername required onChange={handleLastNameChange} label='LastName' username={lastName}/>
          </div>
          <div className={styles.formGroup}>
            <InputAge required onChange={handleAgeChange} label='Age' age={age}/>
          </div>
          <div className={styles.formGroup}>
            <InputPassword required onChange={handlePasswordChange} label='Password' password={password} data-testid="password-textfield"/>
          </div>
          <div className={styles.formGroup}>
            <InputPassword required onChange={handleConfirmPasswordChange} label='Confirm Password' password={confirmPassword} data-testid="confirm-password-textfield"/>
          </div>

          {message && <div role="alert" className={styles.error}>{message}</div>}
          <div className={styles.loginButtons}>
            <ButtonRegister label="Register" disabledLoading={loadingRequest} type='submit' className={styles.buttonLogin}/>
            <ButtonLogin label='Login' disabledLoading={loadingRequest} onClick={() => router.replace("/login")} className={styles.buttonLogin} />
          </div>
        </form>
      </div>
    </div>
  );
}

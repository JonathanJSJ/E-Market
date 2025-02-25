import { ReactNode } from "react";
import NavBar from "@/components/navbar/NavBar";
import styles from "./homePage.module.css";
import Footer from "@/components/footer/Footer";
/* import { headers } from 'next/headers'; 
import { SelectGroups } from '@/services/home/selectGroups';
import { User } from '@/context/ServerAuthContext'; */

type HomeLayoutProps = {
  children: ReactNode;
};

export default async function HomeLayout({ children }: HomeLayoutProps) {
  /* futura recuperação dos dados do usuário vindos do servidor que foram trazidos do backend
  const headersList = headers();
  const userInfoHeader = headersList.get('x-user-info') as string; 
  let user: User = JSON.parse(userInfoHeader);

  const groupsUser = await SelectGroups();
  */

  return (
    <div className={styles.container}>
      <NavBar />
      <div>{children}</div>
      <Footer />
    </div>
  );
}

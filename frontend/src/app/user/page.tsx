"use client";

import {
  Button,
  Divider,
  IconButton,
  Skeleton,
  TextField,
  Typography,
} from "@mui/material";
import styles from "./UserPage.module.css";
import { AccountCircle, Home, Logout, MenuOpen } from "@mui/icons-material";
import { useRouter } from "next/navigation";
import { signOut, useSession } from "next-auth/react";
import { useUserProfile } from "@/hooks/useProfile";
import { useEffect, useState } from "react";
import axios from "axios";
import SellerContract from "@/components/sellerContract/SellerContract";
import { toast } from 'react-toastify';
import "react-toastify/dist/ReactToastify.css";

interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  age: string | null;
  role: string;
}

export default function UserPage() {
  const router = useRouter();
  const { data: session }: any = useSession();
  const { isLoading, error, profile } = useUserProfile()
  const [appliedSeller, setAppliedSeller] = useState(false)
  const [textApplySeller, setTextApplySeller] = useState("Apply to seller")

  if (error) {
    return (
      <div>
        Error loading user data.
      </div>
    )
  }

  const [isSidebarActive, setIsSidebarActive] = useState<boolean>(false);
  const [name, setName] = useState<string | undefined>();
  const [lastName, setLastName] = useState<string | undefined>();
  const [email, setEmail] = useState<string | null | undefined>();
  const [age, setAge] = useState<number | null>();
  const [user, setUser] = useState<User>();
  const [isUpdateAvailable, setIsUpdateAvailable] = useState<boolean>(false);
  const [isSigningContract, setIsSigningContract] = useState<boolean>(false);

  useEffect(() => {
    setUserProfile();
  }, [session?.user]);

  useEffect(() => {
    setName(user?.firstName);
    setLastName(user?.lastName);
    setEmail(user?.email);
    setAge(Number(user?.age));
  }, [user]);

  const setUserProfile = async () => {
    const token = session?.accessToken;

    try {
      const response = await fetch(`/api/proxy/api/user/profile`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const data = await response.json()

      setUser(data);
    } catch (error: any) {
      console.error("Error:", error.response?.data || error.message);
    }
  };

  const handleContractSigning = async () => {
    const token = session?.accessToken;
    try {
      const response = await axios.post(
        `api/proxy/api/seller-applications`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.status === 201) {
        toast.success("Application completed, your request will be reviewed shortly.", { position: "top-center" })
      }
    } catch (error: any) {
      if (error.status === 409) {
        toast.info("Your application is under review. Thanks for your patience.", { position: "top-center" })
      }
    }

    setIsSigningContract(false);
  };

  const handleUpdate = async () => {
    const token = session?.accessToken;

    try {
      const response = await axios.put(
        `/api/proxy/api/user/${user?.id}`,
        { firstName: name, lastName: lastName, age: age, email: email },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      if (response.status === 200) {
        toast.success("Personal information updated sucefully!", { position: "top-center" })
      }
    } catch (error: any) {
      toast.error("Unable to update information, please try again later.", { position: "top-center" })
    }
  };

  return (
    <div className={styles.pageContainer}>
      <div className={styles.userContainer}>
        {/* Sidebar */}
        <aside
          className={`${styles.sidebar} ${isSidebarActive ? styles.active : styles.disabled
            }`}
        >
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <AccountCircle
              style={{
                width: "100px",
                height: "100px",
                color: "var(--red-01)",
              }}
            />
            {user ? (
              <Typography
                style={{
                  display: "flex",
                  justifyContent: "center",
                  width: "200px",
                }}
                variant="h6"
              >
                {user.firstName}
              </Typography>
            ) : (
              <Skeleton variant="rounded" width={180} height={30} />
            )}
            {user ? (
              <Typography
                style={{
                  display: "flex",
                  justifyContent: "center",
                  width: "200px",
                }}
                variant="body1"
              >
                {user.role}
              </Typography>
            ) : (
              <Skeleton
                style={{ marginTop: "5px" }}
                variant="rounded"
                width={60}
                height={20}
              />
            )}
          </div>

          <div className={styles.sidebarOptions}>
            {user?.role === "USER" ? (
              <Button
                variant="outlined"
                style={{ display: "flex", width: "100%" }}
                onClick={() => {
                  setIsSigningContract(true);
                }}
              >
                Become a seller
              </Button>
            ) : user?.role === "SELLER" ? (
              <Button
                variant="contained"
                style={{ display: "flex", width: "100%" }}
                onClick={() => router.replace("/seller")}
              >
                Products page
              </Button>
            ) : (
              user?.role == "ADMIN" && (
                <Button
                  variant="contained"
                  style={{ display: "flex", width: "100%" }}
                  onClick={() => router.replace("/admin/sellers/request")}
                >
                  Administration
                </Button>
              )
            )}

            {
              user && (
                <Button
                  variant="contained"
                  style={{ display: "flex", width: "100%" }}
                  onClick={() => router.replace("/chats")}
                >
                  Chats
                </Button>
              )
            }
          </div>

          <IconButton
            style={{ display: "flex", gap: "10px", borderRadius: "10px", width: "200px"}}
            onClick={() => {
              router.push("/start");
            }}
          >
            <Home /> Home
          </IconButton>
          <IconButton
            style={{ display: "flex", gap: "10px", borderRadius: "10px", width: "200px", color:"var(--red-01)"}}
            onClick={() => {
              signOut({ callbackUrl: "/" });
            }}
          >
            <Logout /> Logout
          </IconButton>
        </aside>

        <Divider variant="middle" flexItem orientation="vertical"></Divider>

        {/* Main */}
        <main className={styles.main}>
          <IconButton
            edge="start"
            className={styles.mobileMenuButton}
            onClick={() => {
              setIsSidebarActive(true);
            }}
          >
            <MenuOpen /> menu
          </IconButton>

          {isSidebarActive && (
            <div
              className={styles.overlay}
              onClick={() => {
                setIsSidebarActive(false);
              }}
            ></div>
          )}

          <div
            style={{
              display: "flex",
              width: "100%",
              justifyContent: "space-between",
              gap: "20px",
            }}
          >
            <Typography variant="h5">Personal Information</Typography>
            {isUpdateAvailable && (
              <Button
                variant="contained"
                style={{ width: "100px", height: "35px" }}
                onClick={handleUpdate}
              >
                Update
              </Button>
            )}
          </div>

          <div className={styles.dataInputs}>
            {user ? (
              <>
                <TextField
                  required
                  label="Name"
                  placeholder="Name"
                  variant="standard"
                  value={name}
                  onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    setIsUpdateAvailable(true);
                    setName(event.target.value);
                  }}
                  slotProps={{
                    inputLabel: {
                      shrink: true,
                    },
                  }}
                />
                <TextField
                  required
                  label="Last Name"
                  placeholder="Last Name"
                  variant="standard"
                  value={lastName}
                  onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    setIsUpdateAvailable(true);
                    setLastName(event.target.value);
                  }}
                  slotProps={{
                    inputLabel: {
                      shrink: true,
                    },
                  }}
                />
                <TextField
                  required
                  label="Email"
                  placeholder="youremail@email.com"
                  variant="standard"
                  style={{ width: "300px" }}
                  value={email}
                  onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    setIsUpdateAvailable(true);
                    setEmail(event.target.value);
                  }}
                  slotProps={{
                    inputLabel: {
                      shrink: true,
                    },
                  }}
                />
                <TextField
                  label="Age"
                  type="number"
                  placeholder="18"
                  variant="standard"
                  style={{ width: "40px" }}
                  value={age}
                  onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                    setIsUpdateAvailable(true);
                    setAge(Number(event.target.value));
                  }}
                  slotProps={{
                    inputLabel: {
                      shrink: true,
                    },
                  }}
                />
              </>
            ) : (
              <>
                <Skeleton variant="rectangular" width={300} height={50} />
                <Skeleton variant="rectangular" width={300} height={50} />
                <Skeleton variant="rectangular" width={300} height={50} />
                <Skeleton variant="rectangular" width={300} height={50} />
              </>
            )}
          </div>
        </main>
      </div>
      {isSigningContract && (
        <SellerContract
          onConfirm={handleContractSigning}
          onCancel={() => {
            setIsSigningContract(false);
          }}
        />
      )}
    </div>
  );
}

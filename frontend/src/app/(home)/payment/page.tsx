"use client";

import {
  Button,
  Divider,
  InputAdornment,
  TextField,
  Typography,
} from "@mui/material";
import styles from "./PaymentPage.module.css";
import {
  CalendarMonth,
  CreditCard,
  CreditScore,
  Password,
  Person,
} from "@mui/icons-material";
import Image from "next/image";
import { useEffect, useState } from "react";
import { useSession } from "next-auth/react";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useRouter } from "next/navigation";

export default function PaymentPage() {
  const [cardNum, setCardNum] = useState<number>();
  const [expiry, setExpiry] = useState(null);
  const [cvv, setCvv] = useState<number>();
  const [cardHolderName, setCardHolderName] = useState<string>();
  const [subtotal, setSubtotal] = useState<number>(0);
  const [shipping, setShipping] = useState<number>(0);
  const [total, setTotal] = useState<number>(0);
  const router = useRouter();
  const { data: session } = useSession();

  useEffect(() => {
    if (session?.user) {
      fetchOrderInfo();
    }
  }, [session?.user]);

  const fetchOrderInfo = async () => {
    const res = await fetch('/api/proxy/api/cart/info', {
      method: "GET",
      headers: {
        Authorization: `Bearer ${session?.accessToken}`,
        "Content-Type": "application/json",
      },
    });
    const responseJson = await res.json();
    setSubtotal(responseJson.subTotal);
    setShipping(responseJson.shippingCost);
    setTotal(responseJson.totalPrice);
  };

  const isInfoValid = () => {
    if (cardNum === null || cardNum?.toString().length != 16) {
      toast.error("Enter a valid credit card number", {
        position: "top-center",
      });
      return false;
    }
    if (expiry === null) {
      toast.error("Select the credit card expiry date", {
        position: "top-center",
      });
      return false;
    }
    if (cvv === null || cvv?.toString().length != 3) {
      toast.error("Enter a valid cvv number", { position: "top-center" });
      return false;
    }
    if (cardHolderName == null) {
      toast.error("Enter the credit card holder name", {
        position: "top-center",
      });
      return false;
    }

    return true;
  };

  const handlePayment = async () => {
    if (isInfoValid()) {
      const res = await fetch('/api/proxy/api/order/create-from-cart', {
        method: "POST",
        headers: {
          Authorization: `Bearer ${session?.accessToken}`,
          "Content-Type": "application/json",
        },
      });

      if (res.status === 201) {
        toast.success(
          "Thank you for your purchase, the order will be processed and your purchase will be prepared for shipping shortly.",
          { position: "top-center" }
        );
        router.push("/cart");
      } else {
        toast.error(
          "Your purchase could not be processed, please check your purchase information or try again later."
        );
      }
    }
  };

  return (
    <div className={styles.pageContainer}>
      <div className={styles.modulesArea}>
        <div className={styles.creditCardModule}>
          <Image
            src={"https://imgur.com/Q3Gmdfw.png"}
            alt="Credit card image"
            width={250}
            height={250}
          />
          <TextField
            style={{ width: "100%" }}
            variant="outlined"
            label="Card number"
            onChange={(e) => {
              setCardNum(Number(e.target.value));
            }}
            slotProps={{
              input: {
                endAdornment: (
                  <InputAdornment position="start">
                    <CreditCard />
                  </InputAdornment>
                ),
              },
            }}
          />
          <div style={{ display: "flex", gap: "10px" }}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker value={expiry} onChange={setExpiry} />
            </LocalizationProvider>
            <TextField
              variant="outlined"
              label="CVV"
              onChange={(e) => {
                setCvv(Number(e.target.value));
              }}
              slotProps={{
                input: {
                  endAdornment: (
                    <InputAdornment position="start">
                      <Password />
                    </InputAdornment>
                  ),
                },
              }}
            />
          </div>
          <TextField
            style={{ width: "100%" }}
            variant="outlined"
            label="Card holder name"
            onChange={(e) => {
              setCardHolderName(e.target.value);
            }}
            slotProps={{
              input: {
                endAdornment: (
                  <InputAdornment position="start">
                    <Person />
                  </InputAdornment>
                ),
              },
            }}
          />
        </div>
        <div className={styles.orderResumeModule}>
          <Image
            src={"https://imgur.com/XEqqFuM.png"}
            alt={"Finish image"}
            width={400}
            height={200}
            className={styles.purchaseImage}
          ></Image>
          <div className={styles.resumeInfo}>
            <Typography variant="h5">Order resume</Typography>
            <Typography>
              Subtotal: {subtotal > 0 && `$${subtotal.toFixed(2)}`}
            </Typography>
            <Typography>
              Shipping: {shipping > 0 && `$${shipping.toFixed(2)}`}
            </Typography>
            <Divider />
            <Typography fontWeight={"bold"}>
              Total: {total > 0 && `$${total.toFixed(2)}`}
            </Typography>
          </div>
          <Button
            startIcon={<CreditScore />}
            variant="contained"
            style={{ height: "50px" }}
            onClick={handlePayment}
          >
            Confirm Payment
          </Button>
        </div>
      </div>
    </div>
  );
}

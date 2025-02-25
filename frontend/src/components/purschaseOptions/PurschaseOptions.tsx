"use client";

import { Button, TextField } from "@mui/material";
import styles from "./PurschaseOptions.module.css";
import { AddShoppingCart, CreditCard } from "@mui/icons-material";
import { useState } from "react";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";

export default function PurschaseOptions({ productId }: { productId: string }) {
  const [quantity, setQuantity] = useState<number>(1);
  const { data: session } = useSession();
  const router = useRouter();

  const handleUnitsChange = (event) => {
    if (event.target.value > 0) {
      setQuantity(event.target.value);
    }
  };

  const addToUserCart = async () => {
    const req = { productId, quantity };
    const res = await fetch(`/api/proxy/api/cart/item`, {
      method: "POST",
      body: JSON.stringify(req),
      headers: {
        Authorization: `Bearer ${session?.accessToken}`,
        "Content-Type": "application/json",
      },
    });
  };

  const handleAddToCart = async () => {
    if (session?.user) {
      await addToUserCart();
    } else {
      router.push("/login");
    }
  };

  const handleBuyNow = async () => {
    if (session?.user) {
      await addToUserCart();
      router.push("/cart");
    } else {
      router.push("/login");
    }
  };

  return (
    <div className={styles.container}>
      <TextField
        label="Quantity"
        type="number"
        variant="filled"
        value={quantity}
        onChange={(event) => {
          handleUnitsChange(event);
        }}
        slotProps={{
          inputLabel: {
            shrink: true,
          },
        }}
        style={{ width: "20%" }}
      />
      <Button
        style={{ height: "55px", width: "35%" }}
        variant="contained"
        color="secondary"
        onClick={handleAddToCart}
        startIcon={<AddShoppingCart />}
      >
        Add to cart
      </Button>
      <Button
        style={{ height: "55px", width: "35%" }}
        variant="contained"
        startIcon={<CreditCard />}
        onClick={handleBuyNow}
      >
        Buy Now
      </Button>
    </div>
  );
}

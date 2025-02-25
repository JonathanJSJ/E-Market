"use client";

import { Button, Divider, TextField, Typography } from "@mui/material";
import styles from "./cart.module.css";
import { useEffect, useState } from "react";
import { CreditCard, LocalShipping } from "@mui/icons-material";
import { useSession } from "next-auth/react";
import CartProduct, {
  CartProductType,
} from "@/components/cartProduct/CartProduct";
import { toast } from "react-toastify";
import { useRouter } from "next/navigation";
import "react-toastify/dist/ReactToastify.css";

interface CartProductResponse {
  id: string;
  productId: string;
  productImage: string;
  productName: string;
  price: number;
  quantity: number;
  shippingPrice: number;
}

function mapCartProdResToCartProd(
  cartProductResponseList: CartProductResponse[]
): CartProductType[] {
  return cartProductResponseList.length > 0
    ? cartProductResponseList.map((product) => ({
      cartProductId: product.id,
      productId: product.productId,
      imageUrl: product.productImage,
      name: product.productName,
      price: product.price,
      units: product.quantity,
    }))
    : [];
}

export default function Cart() {
  const [cartProducts, setCartProducts] = useState<CartProductType[]>([]);
  const [subtotal, setSubtotal] = useState<number>(0);
  const [zipCode, setZipCode] = useState<string>("");
  const [shipping, setShipping] = useState<number>(0);
  const [total, setTotal] = useState<number>(0);
  const { data: session } = useSession();
  const router = useRouter();

  useEffect(() => {
    if (session?.user) {
      fetchUserCart();
    }
  }, [session?.user]);

  useEffect(() => {
    subtotal && shipping ? setTotal(subtotal + shipping) : setTotal(0);
  }, [subtotal, shipping]);

  const fetchUserCart = async () => {
    const res = await fetch('/api/proxy/api/cart', {
      method: "GET",
      headers: {
        Authorization: `Bearer ${session?.accessToken}`,
        "Content-Type": "application/json",
      },
    });
    if (res.status === 200) {
      const responseJson = await res.json();
      const cart: CartProductType[] = mapCartProdResToCartProd(
        responseJson.items
      );

      setSubtotal(responseJson.total);
      setCartProducts(cart);
    }
  };

  const updateItemQuantity = async (productId: string, quantity: number) => {
    const res = await fetch(
      `/api/proxy/api/cart/items/${productId}?quantity=${quantity}`,
      {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${session?.accessToken}`,
          "Content-Type": "application/json",
        },
      }
    );
    const responseJson = await res.json();
    setSubtotal(responseJson.total);
  };

  const handleShippingCalculation = async () => {
    if (zipCode.length === 5) {
      const res = await fetch('/api/proxy/api/cart/info', {
        method: "GET",
        headers: {
          Authorization: `Bearer ${session?.accessToken}`,
          "Content-Type": "application/json",
        },
      });
      const responseJson = await res.json();

      setShipping(responseJson.shippingCost);
    } else {
      toast.error("Please, enter a valid zip code.", {
        position: "top-center",
      });
    }
  };

  const onQuantityChange = async (productId: string, quantity: number) => {
    await updateItemQuantity(productId, quantity);
  };

  const onProductDeletion = async (productId: string) => {
    const res = await fetch(`/api/proxy/api/cart/items/${productId}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${session?.accessToken}`,
        "Content-Type": "application/json",
      },
    });

    if (res.status === 204) {
      fetchUserCart();
    }
  };

  return (
    <div className={styles.pageContainer}>
      <div className={styles.modulesContainer}>
        {/* Cart area */}
        <div className={styles.cartModule}>
          <Typography variant="h4" style={{ marginLeft: "20px" }}>
            My Cart
          </Typography>
          <div className={styles.cartList}>
            {cartProducts.length > 0 ? (
              cartProducts.map((product, index) => (
                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "10px",
                  }}
                  key={index}
                >
                  <CartProduct
                    product={product}
                    onQuantityChange={onQuantityChange}
                    onItemDeletion={onProductDeletion}
                  />
                  <Divider variant="middle" />
                </div>
              ))
            ) : (
              <Typography style={{ marginLeft: "20px" }} variant="body1">
                Empty cart, <a href="/start">add some product.</a>{" "}
              </Typography>
            )}
          </div>
        </div>

        <div className={styles.orderArea}>
          {/* Zipcode area */}
          <div className={styles.zipcodeModule}>
            <Typography variant="h5">Shipping</Typography>
            <TextField
              label={"zip code"}
              value={zipCode}
              onChange={(event) => {
                setZipCode(event.target.value);
              }}
            ></TextField>
            <Button
              variant={"outlined"}
              startIcon={<LocalShipping />}
              onClick={handleShippingCalculation}
            >
              calculate shipping
            </Button>
          </div>

          {/* Order area */}
          <div className={styles.resumeModule}>
            <Typography variant="h5">Order resume</Typography>
            <Typography>
              Subtotal: {subtotal > 0 && `$${subtotal.toFixed(2)}`}
            </Typography>
            <Typography>
              Shipping: {shipping > 0 && `$${shipping.toFixed(2)}`}
            </Typography>
            <Divider />
            <Typography>
              Total: {total > 0 && `$${total.toFixed(2)}`}
            </Typography>
            <Button
              disabled={total === 0}
              variant="contained"
              startIcon={<CreditCard />}
              onClick={() => {
                router.push("/payment");
              }}
            >
              Payment
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

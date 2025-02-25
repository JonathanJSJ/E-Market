"use client";

import { Button, Typography } from "@mui/material";
import style from "./SellerPage.module.css";
import { AccountCircle, Add, Home, KeyboardReturn } from "@mui/icons-material";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import CardProductEdit, {
  CardProductEditProps,
} from "@/components/cardProductEdit/CardProductEdit";
import { useEffect, useRef, useState } from "react";

type ProductResponse = {
  id: string;
  image: string;
  name: string;
  price: number;
  stock: number;
  category: string;
  description: string;
  rating: number;
};

export default function SellerPage() {
  const [isScrollEnable, setIsScrollEnable] = useState<boolean>(true);
  const [products, setProducts] = useState<CardProductEditProps[]>([]);
  const productsListRef = useRef<HTMLDivElement>(null);
  const { data: session } = useSession();
  const router = useRouter();

  useEffect(() => {
    if (session?.user) {
      fetchSellerProducts();
    }
  }, [session?.user]);

  const fetchSellerProducts = async () => {
    const res = await fetch(
      `/api/proxy/api/products/seller/${session?.user.id}?pageNumber=0&pageSize=10`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${session?.accessToken}`,
          "Content-Type": "application/json",
        },
      }
    ).then((r) => r.json());

    setProducts(res.content);
  };

  const handleAddProduct = () => {
    const newProduct: CardProductEditProps = {
      id: "",
      image: "",
      name: "Novo Produto",
      price: 0,
      stock: 0,
      category: "",
      description: "",
      onChange: onChange
    };

    setIsScrollEnable(false);
    setProducts((prevList) => [...prevList, newProduct]);
  };

  const onChange = () => {
    setProducts([])
    fetchSellerProducts();
  };

  useEffect(() => {
    if (productsListRef.current && !isScrollEnable) {
      productsListRef.current.scrollTo({
        top: productsListRef.current.scrollHeight,
        behavior: "smooth",
      });
    }
  }, [products, isScrollEnable]);

  return (
    <div className={style.pageContainer}>
      <div className={style.content}>
        <div className={style.userContext}>
          <div className={style.profileArea}>
            <AccountCircle
              style={{
                width: "60px",
                height: "60px",
                color: "white",
              }}
            />
            <Typography color="white" fontSize={22}>
              {session?.user?.name}
            </Typography>
          </div>
          <div className={style.optionsArea}>
            <Button
              size="large"
              color="secondary"
              variant="contained"
              startIcon={<KeyboardReturn />}
              style={{ width: "180px" }}
              onClick={() => {
                router.push("/user");
              }}
            >
              GO BACK
            </Button>
            <Button
              size="large"
              color="secondary"
              variant="contained"
              startIcon={<Home />}
              style={{ width: "180px" }}
              onClick={() => {
                router.push("/start");
              }}
            >
              HOME
            </Button>
          </div>
        </div>
        <div className={style.productsArea}>
          <div className={style.productsHeader}>
            <Typography variant="h5">Your products</Typography>
            <Button
              size="large"
              variant="outlined"
              startIcon={<Add />}
              onClick={handleAddProduct}
            >
              ADD PRODUCT
            </Button>
          </div>
          <div className={style.productsList} ref={productsListRef}>
            {products &&
              products.map((product, index) => (
                <CardProductEdit
                  id={product.id}
                  image={product.image}
                  name={product.name}
                  price={product.price}
                  stock={product.stock}
                  category={product.category}
                  description={product.description}
                  onChange={onChange}
                ></CardProductEdit>
              ))}
          </div>
        </div>
      </div>
    </div>
  );
}

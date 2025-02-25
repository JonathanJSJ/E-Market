import {
  CardActionArea,
  IconButton,
  Skeleton,
  Typography,
} from "@mui/material";
import styles from "./CartProduct.module.css";
import { Add, Delete, Remove } from "@mui/icons-material";
import { useEffect, useState } from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";

export type CartProductType = {
  cartProductId: string;
  productId: string;
  imageUrl: string;
  name: string;
  price: number;
  units: number;
};

interface CartProductProps {
  product: CartProductType;
  onQuantityChange: (productId: string, quantity: number) => void;
  onItemDeletion: (productId: string) => void;
}

export default function CartProduct({
  product,
  onQuantityChange,
  onItemDeletion
}: CartProductProps) {
  const [orderUnits, setOrderUnits] = useState<number>(product.units);
  const [total, setTotal] = useState<number>(product.units * product.price);
  const [isImageLoading, setIsImageLoading] = useState<boolean>(true);
  const router = useRouter();

  useEffect(() => {
    setTotal(orderUnits * product.price);
  }, [orderUnits]);

  const handleUnitsReduction = () => {
    if (orderUnits > 1) {
      onQuantityChange(product.productId, orderUnits - 1);
      setOrderUnits((u) => u - 1);
    }
  };

  const handleUnitsAddition = () => {
    onQuantityChange(product.productId, orderUnits + 1);
    setOrderUnits((u) => u + 1);
  };

  const handleItemDeletion = () =>{
    onItemDeletion(product.productId)
  }

  const handleProductRedirection = () => {
    router.push(`/product/${product.productId}`);
  };

  return (
    <div className={styles.container}>
      <CardActionArea
        className={styles.product}
        onClick={handleProductRedirection}
      >
        {product.imageUrl ? (
          <div style={{ position: "relative", width: 100, height: 100 }}>
            {isImageLoading && (
              <Skeleton
                width={100}
                height={100}
                variant="rounded"
                style={{ position: "absolute", top: 0, left: 0 }}
              />
            )}
            <Image
              src={product.imageUrl}
              alt={"product photo"}
              width={100}
              height={100}
              onLoadingComplete={() => setIsImageLoading(false)}
              style={{
                objectFit: "contain",
                backgroundColor: "white",
                borderRadius: "15px",
              }}
            />
          </div>
        ) : (
          <Skeleton width={100} height={100} variant="rounded" />
        )}

        <div className={styles.productInfo}>
          <Typography variant="h5" className={styles.title}>
            {product.name}
          </Typography>
          <Typography>${product.price.toFixed(2)}</Typography>
        </div>
      </CardActionArea>

      <div className={styles.editionArea}>
        <div className={styles.unitsInfo}>
          <div className={styles.counter}>
            <IconButton onClick={handleUnitsReduction}>
              <Remove />
            </IconButton>
            <Typography>{orderUnits}</Typography>
            <IconButton onClick={handleUnitsAddition}>
              <Add />
            </IconButton>
          </div>

          <Typography style={{ whiteSpace: "nowrap" }}>
            Total: ${total.toFixed(2)}
          </Typography>
        </div>
        <IconButton style={{ width: "50px", height: "50px" }} onClick={handleItemDeletion}>
          <Delete />
        </IconButton>
      </div>
    </div>
  );
}

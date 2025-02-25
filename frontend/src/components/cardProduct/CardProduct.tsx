import React from "react";
import {
  Card,
  CardContent,
  Typography,
  CardMedia,
  Skeleton,
  CardActionArea,
  Rating,
} from "@mui/material";
import styles from "./CardProduct.module.css";
import { useRouter } from "next/navigation";
import { Star } from "@mui/icons-material";

export interface ProductProps {
  id: string;
  imageUrl: string;
  title: string;
  price: number;
  description: string;
  rating: number;
}

export default function CardProduct({
  id,
  imageUrl,
  title,
  description,
  rating,
  price,
}: ProductProps) {
  const router = useRouter();
  return (
    <Card className={styles.cardWraper}>
      <CardActionArea
        className={styles.cardContainer}
        onClick={() => {
          router.push(`/product/${id}`);
        }}
      >
        {imageUrl ? (
          <CardMedia className={styles.cardMedia}>
            <img alt={title} src={imageUrl} className={styles.cardImage} />
          </CardMedia>
        ) : (
          <Skeleton variant="rectangular" className={styles.cardMedia} />
        )}

        <CardContent className={styles.cardContent}>
          {title ? (
            <Typography
              gutterBottom
              component="div"
              sx={{
                fontSize: { xs: "0.8rem", md: "1rem" },
                width: "100%",
                whiteSpace: "nowrap",
                overflow: "hidden",
                textOverflow: "ellipsis",
              }}
            >
              {title}
            </Typography>
          ) : (
            <Skeleton variant="text" width="60%" />
          )}
          {price ? (
            <Typography
              variant="body1"
              color="text.primary"
              sx={{ fontSize: { xs: "0.8rem", md: "1.1rem" } }}
            >
              $ {price.toFixed(2)}
            </Typography>
          ) : (
            <Skeleton variant="text" width="40%" />
          )}

          {description ? (
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{
                fontSize: { xs: "0.8rem" },
                weight: "100%",
                height: "100%",
                maxHeight: "57px",
                overflow: "hidden",
              }}
            >
              {description}
            </Typography>
          ) : (
            <>
              <Skeleton variant="text" width="100%" />
              <Skeleton variant="text" width="100%" />
            </>
          )}
          <Rating readOnly size="small" precision={0.5} defaultValue={rating} />
        </CardContent>
      </CardActionArea>
    </Card>
  );
}

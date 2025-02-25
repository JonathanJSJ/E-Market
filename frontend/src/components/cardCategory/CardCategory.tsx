"use client"

import React from "react";
import {
  Card,
  CardContent,
  Typography,
  CardMedia,
  Skeleton,
  CardActionArea,
} from "@mui/material";
import styles from "./CardCategory.module.css";
import { useRouter } from "next/navigation";

interface CategoryProps {
  imageUrl: string;
  title: string;
}

export default function CategoryCard({ imageUrl, title }: CategoryProps) {
  const router = useRouter()
  return (
    <CardActionArea onClick={()=>{router.push(`/search?category=${title}`)}}>
      <Card className={styles.card}>
        {imageUrl ? (
          <CardMedia className={styles.cardMedia}>
            <div className={styles.overlay} />
            <img alt={title} src={imageUrl} className={styles.categoryImage} />
          </CardMedia>
        ) : (
          <Skeleton variant="rounded" width={150} height={140} />
        )}
        <CardContent className={styles.categoryTitle}>
          {title ? (
            <Typography sx={{ fontSize: "0.4" }} component="div">
              {title}
            </Typography>
          ) : (
            <Skeleton variant="text" width="60%" />
          )}
        </CardContent>
      </Card>
    </CardActionArea>
  );
}

"use client";

import styles from "./ProductsGroup.module.css";
import CardProduct from "../cardProduct/CardProduct";
import { useEffect, useRef, useState } from "react";
import { Typography } from "@mui/material";

type Product = {
  id: string;
  imageUrl: string;
  title: string;
  price: number;
  description: string;
  rating: number;
};

interface ProductsProp {
  groupName: string;
  groupProducts: Product[];
}

export default function ProductsGroup({
  groupName,
  groupProducts,
}: ProductsProp) {
  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const [canScrollLeft, setCanScrollLeft] = useState(false);
  const [canScrollRight, setCanScrollRight] = useState(false);

  const checkScrollPosition = () => {
    const container = scrollContainerRef.current;

    if (container) {
      setCanScrollLeft(container.scrollLeft > 0);
      setCanScrollRight(
        container.scrollLeft + container.clientWidth < container.scrollWidth
      );
    }
  };

  const scrollLeft = () => {
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollBy({ left: -500, behavior: "smooth" });
    }
  };

  const scrollRight = () => {
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollBy({ left: 500, behavior: "smooth" });
    }
  };

  useEffect(() => {
    checkScrollPosition();
    const container = scrollContainerRef.current;
    if (container) {
      container.addEventListener("scroll", checkScrollPosition);
    }
    return () => {
      if (container) {
        container.removeEventListener("scroll", checkScrollPosition);
      }
    };
  }, []);

  return (
    <div className={styles.container}>
      <Typography variant="h5" className={styles.categoryLabel}>
        {groupName}
      </Typography>
      <div className={styles.productsContainer}>
        {canScrollLeft && (
          <button className={styles.scrollButton} onClick={scrollLeft}>
            ◀
          </button>
        )}
        <div className={styles.productsGroup} ref={scrollContainerRef}>
          {groupProducts.map((product, index) => (
            <CardProduct
              key={index}
              id={product.id}
              imageUrl={product.imageUrl}
              title={product.title}
              price={product.price}
              description={product.description}
              rating={product.rating}
            />
          ))}
          {canScrollRight && (
            <button
              className={`${styles.scrollButton} ${styles.rightButton}`}
              onClick={scrollRight}
            >
              ▶
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

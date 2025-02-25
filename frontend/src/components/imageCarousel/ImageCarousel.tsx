"use client";

import React, { useState, useEffect } from "react";
import styles from "./ImageCarousel.module.css";

interface Banner {
  id: number;
  image: string;
  altText: string;
}

interface CarouselProps {
  banners: Banner[];
}

const ImageCarousel: React.FC<CarouselProps> = ({ banners }) => {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentIndex((prevIndex) => (prevIndex + 1) % banners.length);
    }, 10000);

    return () => clearInterval(interval);
  }, [currentIndex]);

  const handlePrev = () => {
    setCurrentIndex(
      (prevIndex) => (prevIndex - 1 + banners.length) % banners.length
    );
  };

  const handleNext = () => {
    setCurrentIndex((prevIndex) => (prevIndex + 1) % banners.length);
  };

  return (
    <div className={styles.carousel}>
      <button
        onClick={handlePrev}
        className={`${styles.carouselButton} ${styles.prevButton}`}
      >
        &#10094;
      </button>
      <div className={styles.carouselContent}>
        {banners.map((banner, index) => (
          <div
            key={banner.id}
            className={`${styles.banner} ${
              index === currentIndex ? styles.active : ""
            }`}
          >
            <img
              src={banner.image}
              alt={banner.altText}
              className={styles.bannerImage}
            />
          </div>
        ))}
      </div>
      <button
        onClick={handleNext}
        className={`${styles.carouselButton} ${styles.nextButton}`}
      >
        &#10095;
      </button>
    </div>
  );
};

export default ImageCarousel;

"use client";

import CardProduct from "@/components/cardProduct/CardProduct";
import {
  Button,
  Divider,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Pagination,
  Rating,
  Select,
  SelectChangeEvent,
  Slider,
  Typography,
} from "@mui/material";
import styles from "./searchPage.module.css";
import { useEffect, useRef, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { FilterAlt } from "@mui/icons-material";

type Product = {
  id: string;
  imageUrl: string;
  title: string;
  price: number;
  description: string;
  rating: number;
};

type ProductResponse = {
  id: string;
  image: string;
  name: string;
  price: number;
  description: string;
  rating: number;
};

const mapProducts = (
  products: ProductResponse[] | undefined | null
): Product[] => {
  if (!Array.isArray(products)) {
    return [];
  }

  return products.map((product) => ({
    id: product.id,
    imageUrl: product.image,
    title: product.name,
    price: product.price,
    description: product.description,
    rating: product.rating,
  }));
};

export default function SearchPage() {
  const router = useRouter();
  const [products, setProducts] = useState<Product[]>([]);
  const [name, setName] = useState<string | null>("");
  const [categories, setCategories] = useState<string[]>([]);
  const [category, setCategory] = useState<string | null>("");
  const [priceRange, setPriceRange] = useState<number[]>([0, 10000]);
  const [rate, setRate] = useState<number>(0);
  const [page, setPage] = useState(1);
  const [pagesNumber, setPagesNumber] = useState(1);
  const [isFiltersActive, setIsFiltersActive] = useState(false);
  const searchParams = useSearchParams();
  const MIN = 0;
  const MAX = 10000;
  const baseUrl = process.env.NEXT_PUBLIC_API_URL;
  const currentController = useRef<AbortController | null>(null);

  async function fetchCategories() {
    try {
      const response = await fetch(`/api/proxy/api/categories`);
      const data = await response.json();

      const categoriesName = data.map((category) => category.name);
      setCategories(categoriesName);
    } catch (error) {
      console.error("Error:", error);
    }
  }

  useEffect(() => {
    fetchCategories();
  }, []);

  async function fetchProducts() {
    if (currentController.current) {
      currentController.current.abort();
    }
    currentController.current = new AbortController();
    const { signal } = currentController.current;
    
    try {
      const response = await fetch(
        `/api/proxy/api/products?pageSize=18&pageNumber=${page - 1}${name ? `&name=${name}&` : ""
        }&${category ? `category=${category}&` : ""}minPrice=${priceRange[0]
        }&maxPrice=${priceRange[1]}&minRate=${rate}`
        ,{signal});
        const data = await response.json();
        
        setPagesNumber(data.page.totalPages);
        const products: Product[] = mapProducts(data.content);
        setProducts(products);
    } catch (error: any) {
      if (error.name === 'AbortError') {
        console.log("Fetch canceled");
      } else {
      console.error("Error:", error);
      }
    }
  }

  useEffect(() => {
    if (searchParams.get("name")) {
      setName(searchParams.get("name"));
    }
    if (searchParams.get("category")) {
      setCategory(searchParams.get("category"));
    }
  }, [searchParams.get("name"), searchParams.get("category")]);

  useEffect(() => {
    fetchProducts();
  }, [page, name, category]);

  const handleCategoryChange = (event: SelectChangeEvent) => {
    setCategory(event.target.value as string);
  };

  const handlePriceChange = (event: Event, newValue: number | number[]) => {
    setPriceRange(newValue as number[]);
  };

  const handleRatingChange = (event: Event, rate: number) => {
    setRate(rate);
  };

  const handlePageChange = (event: Event, value: number) => {
    setPage(value);
  };

  const handleSearchClick = () => {
    setPage(1);
    fetchProducts();
  };

  const handleResetClick = () => {
    setCategory("");
    setPriceRange([MIN, MAX]);
    setRate(0);
    setName("");
    setPage(1);
    router.push("/search?");
  };

  return (
    <div className={styles.containerFrame}>
      {/* Filters sidebar */}
      <aside
        className={`${styles.filtersSidebar} ${isFiltersActive ? styles.active : styles.disabled
          }`}
      >
        <div>
          <Typography variant="h5">Filters</Typography>
          <Divider
            variant="middle"
            flexItem
            style={{ backgroundColor: "red" }}
          />
        </div>

        <div className={styles.filtersOptions}>
          <FormControl style={{ width: "80%" }}>
            <InputLabel id="category-select-label">Category</InputLabel>
            <Select
              labelId="category-select-label"
              value={category}
              label="Category"
              onChange={handleCategoryChange}
            >
              {categories &&
                categories.map((category, index) => {
                  return (
                    <MenuItem key={index} value={category}>
                      {category}
                    </MenuItem>
                  );
                })}
            </Select>
          </FormControl>

          <div
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              width: "100%",
              gap: "35px",
            }}
          >
            <Typography component="legend">Price range:</Typography>
            <Slider
              getAriaLabel={() => "Price"}
              step={1000}
              marks
              value={priceRange}
              onChange={handlePriceChange}
              valueLabelDisplay="on"
              max={MAX}
              style={{
                width: "70%",
              }}
            />
          </div>

          <div
            style={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              width: "100%",
              gap: "10px",
            }}
          >
            <Typography component="legend">Rating above:</Typography>
            <Rating
              value={rate}
              size="large"
              max={5}
              onChange={handleRatingChange}
            />
          </div>
        </div>

        <div
          style={{
            display: "flex",
            flexDirection: "column",
            width: "80%",
            gap: "15px",
          }}
        >
          <Button variant="contained" onClick={handleSearchClick}>
            Search
          </Button>

          <Button variant="outlined" onClick={handleResetClick}>
            Reset
          </Button>
        </div>
      </aside>

      {/* Filter button for mobile */}
      <IconButton
        onClick={() => setIsFiltersActive(!isFiltersActive)}
        className={styles.filtersButton}
      >
        <FilterAlt />
      </IconButton>

      {/* Products Container*/}
      <div className={styles.productsContainer}>
        {isFiltersActive && (
          <div
            className={styles.overlay}
            onClick={() => {
              setIsFiltersActive(false);
            }}
          ></div>
        )}
        {name && (
          <>
            <Typography style={{ marginTop: "20px" }} variant="h5">
              {`Products related to "${searchParams.get("name")}"`}
            </Typography>
            <Divider style={{ marginTop: "10px" }} />
          </>
        )}
        <div className={styles.products}>
          {products.length > 0 ? (
            products.map((product: Product, index) => {
              return (
                <CardProduct
                  key={index}
                  id={product.id}
                  imageUrl={product.imageUrl}
                  title={product.title}
                  price={product.price}
                  description={product.description}
                  rating={product.rating}
                />
              );
            })
          ) : (
            <Typography style={{ color: "black" }} variant="h5">
              No products found.
            </Typography>
          )}
        </div>
        {pagesNumber > 1 && (
          <Pagination
            className={styles.pagination}
            count={pagesNumber}
            page={page}
            onChange={handlePageChange}
            variant="outlined"
            color="primary"
          />
        )}
      </div>
    </div>
  );
}

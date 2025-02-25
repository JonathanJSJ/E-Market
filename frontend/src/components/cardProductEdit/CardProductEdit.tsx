"use client";

import {
  FormControl,
  IconButton,
  Input,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from "@mui/material";
import styles from "./CardProductEdit.module.css";
import { useEffect, useState } from "react";
import { Delete, FileUpload, Save, SaveAs } from "@mui/icons-material";
import Image from "next/image";
import { useSession } from "next-auth/react";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export interface CardProductEditProps {
  id: string | null;
  image: string;
  name: string;
  price: number;
  stock: number;
  category: string;
  description: string;
  onChange: () => void;
}

export default function CardProductEdit({
  id,
  image,
  name,
  price,
  stock,
  category,
  description,
  onChange,
}: CardProductEditProps) {
  const [idState, setIdState] = useState<string>(id)
  const [imageState, setImageState] = useState<string>(image);
  const [nameState, setNameState] = useState<string>(name);
  const [priceState, setPriceState] = useState<number>(price);
  const [unitsState, setUnitsState] = useState<number>(stock);
  const [categoryState, setCategoryState] = useState<string>(category);
  const [categories, setCategories] = useState<string[]>([]);
  const [descriptionState, setDescriptionState] = useState<string>(description);

  const { data: session } = useSession();

  useEffect(() => {
    fetchCategories();
  });

  const fetchCategories = async () => {
    const response = await fetch(`/api/proxy/api/categories`).then((res) =>
      res.json()
    );
    const categoriesName = response.map(
      (category: { id: string; name: string }) => category.name
    );
    setCategories(categoriesName);
  };

  const editItem = async () => {
    const response = await fetch(`/api/proxy/api/products/${id}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${session?.accessToken}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name: nameState,
        description: descriptionState,
        image: imageState,
        price: priceState,
        stock: unitsState,
        category: categoryState,
      }),
    });
    if (response.status === 200) {
      toast.success("Product updated sucefully!", { position: "top-center" })
    }

  };

  const deleteItem = async () => {
    const response = await fetch(`/api/proxy/api/products/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${session?.accessToken}`,
        "Content-Type": "application/json",
      },
    });

    onChange()
  };

  const addItem = async () => {
    const response = await fetch(`/api/proxy/api/products`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${session?.accessToken}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        image: imageState,
        name: nameState,
        description: descriptionState,
        price: priceState,
        stock: unitsState,
        category: categoryState,
      }),
    }).then((res) => res.json());

    setIdState(response.id)
  };

  return (
    <div className={styles.container}>
      <div className={styles.midia}>
        <Image
          src={imageState}
          alt="product img"
          width={200}
          height={200}
          style={{ objectFit: "contain" }}
        ></Image>
      </div>
      <div className={styles.productEditionArea}>
        <div className={styles.fieldsAndOptions}>
          <div className={styles.productInfoFields}>
            <TextField
              label="Product name"
              value={nameState}
              style={{ width: "420px" }}
              onChange={(e) => {
                setNameState(e.target.value);
              }}
            ></TextField>
            <TextField
              label="Product price"
              type="number"
              style={{ width: "140px" }}
              value={priceState}
              onChange={(e) => {
                setPriceState(Number(e.target.value));
              }}
            ></TextField>
            <TextField
              label="Units"
              type="number"
              style={{ width: "100px" }}
              value={unitsState}
              onChange={(e) => {
                setUnitsState(Number(e.target.value));
              }}
            ></TextField>
            <FormControl style={{ width: "160px" }}>
              <InputLabel>Category</InputLabel>
              <Select
                value={categoryState}
                label="Category"
                onChange={(e) => {
                  setCategoryState(e.target.value);
                }}
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
          </div>
          <div className={styles.options}>
            {idState ? (
              <>
                <IconButton className={styles.optionsButton} onClick={editItem}>
                  <SaveAs />
                </IconButton>
                <IconButton
                  className={styles.optionsButton}
                  onClick={deleteItem}
                >
                  <Delete />
                </IconButton>
              </>
            ) : (
              <IconButton className={styles.optionsButton} onClick={addItem}>
                <Save />
              </IconButton>
            )}
          </div>
        </div>
        <Input
          multiline
          fullWidth
          placeholder="Add a short description..."
          disableUnderline={true}
          className={styles.descriptionInput}
          value={descriptionState}
          onChange={(event) => {
            setDescriptionState(event.target.value);
          }}
        />
      </div>
    </div>
  );
}

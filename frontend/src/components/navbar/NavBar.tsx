"use client";

import React, { KeyboardEvent, useState } from "react";
import {
  AppBar,
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Button,
  Divider,
  Input,
  InputAdornment,
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import styles from "./NavBar.module.css";
import { usePathname, useRouter } from "next/navigation";
import Image from "next/image";
import {
  AccountCircle,
  Person,
  Search,
  ShoppingBag,
  ShoppingCart,
} from "@mui/icons-material";
import { useSession } from "next-auth/react";

export default function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const router = useRouter();
  const { data: session } = useSession();
  const nameUser = session?.user?.name;

  const handleDrawerToggle = () => {
    setMenuOpen(!menuOpen);
  };

  const handleNavigation = (route: string) => {
    setMenuOpen(false);
    router.push(route);
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && searchTerm.trim()) {
      setSearchTerm("");
      router.push(`/search?name=${encodeURIComponent(searchTerm)}`);
    }
  };

  const routerPathname = usePathname()
    ?.replace(/\/home/gi, "")
    .toLocaleLowerCase();

  return (
    <div>
      <AppBar position="fixed" className={styles.appBar}>
        <div className={styles.logoContainer}>
          <Image
            className={styles.logo}
            src="https://imgur.com/kDgIfIX.png"
            alt="Logo"
            width={160}
            height={50}
            onClick={() => handleNavigation("/start")}
          />
          <Input
            
            type="search"
            placeholder="Search product"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={handleKeyDown}
            className={styles.searchField}
            disableUnderline={true}
            startAdornment={
              <InputAdornment position="start">
                <Search />
              </InputAdornment>
            }
          />
        </div>

        <IconButton
          edge="start"
          color="inherit"
          aria-label="menu"
          onClick={handleDrawerToggle}
          className={styles.menuButton}
          data-testid="menu-button"
        >
          <MenuIcon className={styles.menuButton} />
        </IconButton>

        {/* Desktop Navigation */}
        <div className={styles.navItems}>
          <Button
            className={styles.navItemSelected}
            onClick={() => handleNavigation("/search")}
            data-testid="products-button"
          >
            <ShoppingBag />
            products
          </Button>

          {session?.user && (
            <Button
              className={styles.navItemSelected}
              onClick={() => handleNavigation("/cart")}
              data-testid="cart-button"
            >
              <ShoppingCart />
              cart
            </Button>
          )}

          <Button
            className={
              routerPathname === "/login"
                ? styles.navItemSelect
                : styles.navItem
            }
            onClick={() => {
              if (nameUser) {
                handleNavigation("/user");
              } else {
                handleNavigation("/login");
              }
            }}
            data-testid="login-user-button"
          >
            <Person />
            {nameUser ? nameUser : "login"}
          </Button>
        </div>
      </AppBar>

      {/* Drawer for mobile */}
      <Drawer
        anchor="right"
        open={menuOpen}
        onClose={handleDrawerToggle}
        className={styles.drawer}
      >
        <List className={styles.drawerList}>
          <ListItem
            onClick={() => {
              if (nameUser) {
                handleNavigation("user");
              } else {
                handleNavigation("login");
              }
            }}
          >
            <ListItemIcon>
              <AccountCircle />
            </ListItemIcon>
            <ListItemText primary="Profile" />
          </ListItem>
          <Divider variant="middle" />
          <ListItem onClick={() => handleNavigation("/search")}>
            <ListItemIcon>
              <ShoppingBag />
            </ListItemIcon>
            <ListItemText primary="Products" />
          </ListItem>
          {session?.user && (
            <ListItem onClick={() => handleNavigation("/cart")}>
              <ListItemIcon>
                <ShoppingCart />
              </ListItemIcon>
              <ListItemText primary="Cart" />
            </ListItem>
          )}
        </List>
      </Drawer>
    </div>
  );
}

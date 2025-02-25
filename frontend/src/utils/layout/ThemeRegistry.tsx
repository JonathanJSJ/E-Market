"use client";

import * as React from "react";
import CssBaseline from "@mui/material/CssBaseline";
import { createTheme, ThemeOptions, ThemeProvider } from "@mui/material/styles";
import { Roboto } from "next/font/google";
import { NextAppDirEmotionCacheProvider } from "./createEmotionCache";

const roboto = Roboto({
  weight: ["300", "400", "500", "700"],
  style: ["normal", "italic"],
  subsets: ["latin"],
});

const themeOptions: ThemeOptions = {
  typography: {
    fontFamily: roboto.style.fontFamily,
  },
  palette: {
    background: {
      default: "#f5f5f5",
    },
    primary: {
      main: '#d32f2f',
    },
    secondary: {
      main: '#ffffff'
    }
  },
  components: {
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiInputBase-root': {
            height: 56,
            boxSizing: 'border-box',
          },
        },
      },
    },
    MuiInputBase: {
      styleOverrides: {
        root: {
          height: 56,
          boxSizing: 'border-box',
        },
      },
    },
  }
};

const theme = createTheme(themeOptions);

export default function ThemeRegistry({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <NextAppDirEmotionCacheProvider options={{ key: "mui", prepend: true }}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </ThemeProvider>
    </NextAppDirEmotionCacheProvider>
  );
}
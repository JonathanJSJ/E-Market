import { Avatar, Rating, Typography } from "@mui/material";
import styles from "./UserFeedback.module.css";

interface Seller {
  name: string;
  ratingNumber: number;
  description: string;
}

export default function UserFeedback(seller: Seller) {
  return (
    <div className={styles.container}>
      <div style={{ display: "flex", alignItems: "center", gap: "15px" }}>
        <Avatar  sx={{ bgcolor: "var(--red-01)" }}>{seller.name.charAt(0)}</Avatar>
        <Typography fontSize={22}>{seller.name}</Typography>
      </div>
      <Rating precision={0.5} readOnly value={seller.ratingNumber} />
      <Typography>{seller.description}</Typography>
    </div>
  );
}

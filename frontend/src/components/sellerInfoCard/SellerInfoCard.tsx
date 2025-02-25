import { Avatar, Divider, Rating, Typography } from "@mui/material";
import styles from "./SellerInfoCard.module.css";

interface SellerInfo {
    name: string,
    ratingValue: number,
    startDate: string
}

export function SellerInfoCard(sellerInfo: SellerInfo) {
  return (
    <div className={styles.cardContainer}>
      <Typography variant="h5">About the seller</Typography>
      <div className={styles.content}>
        <Avatar
          sx={{ bgcolor: "var(--red-01)", width: "100px", height: "100px" }}
        >
          S
        </Avatar>
        <div>
          <Typography variant="h5"> {sellerInfo.name}</Typography>
          <Rating readOnly precision={0.5} value= {sellerInfo.ratingValue}></Rating>
          <Typography>Seller since: {sellerInfo.startDate}</Typography>
        </div>
      </div>
    </div>
  );
}

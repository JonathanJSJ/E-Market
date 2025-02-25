import { Check, Close } from "@mui/icons-material";
import { Button, Typography } from "@mui/material";
import styles from "./SellerContract.module.css";

export default function SellerContract({onConfirm, onCancel}) {
  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <Typography variant="h4">Terms of Use</Typography>
        <Typography
          align="justify"
          className={styles.text}
        >
            Welcome to E-Market! By using our seller application, you agree to these
            Terms of Use. This agreement governs your access to and use of the
            application provided by E-Market Co.. To participate as a seller, you
            must provide accurate registration details, comply with applicable
            laws, and adhere to marketplace policies. Failure to meet these
            requirements may result in account suspension or termination.
          <p></p>
          <p>
            As a seller, you are responsible for managing your product listings,
            ensuring the legality and quality of your offerings, and resolving
            customer disputes in good faith. The application may charge fees as
            outlined in our Fee Schedule, which will be deducted from your
            payouts. Payments and transactions are processed by third-party
            providers, and we are not liable for issues caused by their systems.
          </p>
          We reserve the right to modify or terminate these Terms or the
          application at any time. While we strive to provide reliable services,
          we disclaim liability for indirect or consequential damages arising
          from its use. These Terms are governed by the laws of the State of
          California, and any disputes will be resolved in the courts of this
          region.
        </Typography>
        <div style={{ display: "flex", marginTop: "30px", gap: "20px" }}>
          <Button onClick={onCancel} startIcon={<Close />} variant="outlined">
            Cancel
          </Button>
          <Button onClick={onConfirm} startIcon={<Check />} variant="contained">
            Agree
          </Button>
        </div>
      </div>
    </div>
  );
}

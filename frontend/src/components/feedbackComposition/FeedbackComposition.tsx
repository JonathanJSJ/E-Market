"use client";

import { Button, Input, Rating, Typography } from "@mui/material";
import styles from "./FeedbackComposition.module.css";
import { Send } from "@mui/icons-material";
import { useEffect, useState } from "react";
import { useSession } from "next-auth/react";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function FeedbackComposition({
  productId,
}: {
  productId: string;
}) {
  const [productRating, setProductRating] = useState<number>(0);
  const [description, setDescription] = useState<string>("");
  const [canUserGiveFeedback, setCanUserGiveFeedback] =
    useState<boolean>(false);
  const { data: session } = useSession();

  useEffect(() => {
    if (session?.user) {
      verifyIfUserCanShareFeedback();
    }
  }, [session?.user]);

  const handleFeedbackSubmit = async () => {
    const req = {
      comment: description,
      rate: productRating,
      productId: productId,
    };
    const res = await fetch(`/api/proxy/api/rating`, {
      method: "POST",
      body: JSON.stringify(req),
      headers: {
        Authorization: `Bearer ${session?.accessToken}`,
        "Content-Type": "application/json",
      },
    });
    if (res.status === 200) {
      toast.success("Thank you for sharing your experience!", {
        position: "top-center",
      });
      setCanUserGiveFeedback(false)
    } else {
      toast.error("Sorry, an error occurred, please try again later.", {
        position: "top-center",
      });
    }
  };

  const verifyIfUserCanShareFeedback = async () => {
    if (session?.accessToken) {
      const res = await fetch(
        `/api/proxy/api/rating/product/${productId}/confirmation`,
        {
          headers: {
            Authorization: `Bearer ${session?.accessToken}`,
            "Content-Type": "application/json",
          },
        }
      ).then((res) => res.json());
      setCanUserGiveFeedback(res);
    }
  };

  return (
    <div
      className={`${styles.container} ${canUserGiveFeedback === false ? `${styles.disabled}` : {}
        }`}
    >
      <Typography fontSize={20} variant="body1">
        Share your feedback:
      </Typography>
      <div className={styles.ratingArea}>
        <div className={styles.ratingOption}>
          <Typography>Product rating:</Typography>
          <Rating
            precision={0.5}
            value={productRating}
            onChange={(event, newValue) => {
              setProductRating(newValue);
            }}
          ></Rating>
        </div>
      </div>
      <Input
        multiline
        fullWidth
        placeholder="Add a short comment..."
        disableUnderline={true}
        className={styles.descriptionInput}
        value={description}
        onChange={(event) => {
          setDescription(event.target.value);
        }}
      />
      <div style={{ display: "flex", justifyContent: "flex-end" }}>
        <Button
          startIcon={<Send />}
          variant="contained"
          onClick={handleFeedbackSubmit}
        >
          Share
        </Button>
      </div>
    </div>
  );
}

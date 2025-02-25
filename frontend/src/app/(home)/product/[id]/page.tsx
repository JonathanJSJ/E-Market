import ProductsGroup from "@/components/productsGroup/ProductsGroup";
import styles from "./productPage.module.css";
import { Button, Divider, Rating, Skeleton, Typography } from "@mui/material";
import Image from "next/image";
import { SellerInfoCard } from "@/components/sellerInfoCard/SellerInfoCard";
import UserFeedback from "@/components/userFeedback/UserFeedback";
import { LiveHelp, SupportAgent } from "@mui/icons-material";
import { ProductProps } from "@/components/cardProduct/CardProduct";
import { fetchServer } from "@/services/fetchServer";
import PurschaseOptions from "@/components/purschaseOptions/PurschaseOptions";
import FeedbackComposition from "@/components/feedbackComposition/FeedbackComposition";
import { redirect } from "next/navigation";

type ProductResponse = {
  id: string;
  image: string;
  name: string;
  price: number;
  description: string;
  rating: number;
};

function formatDateToMMDDYYYY(dateString: string): string {
  const date = new Date(dateString);

  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  const year = date.getFullYear().toString();

  return `${month}/${day}/${year}`;
}

const mapProducts = (
  products: ProductResponse[] | undefined | null,
  excludedId: string
): ProductProps[] => {
  if (!Array.isArray(products)) {
    return [];
  }

  return products
    .filter((product) => product.id !== excludedId)
    .map((product) => ({
      id: product.id,
      imageUrl: product.image,
      title: product.name,
      price: product.price,
      description: product.description,
      rating: product.rating,
    }));
};

export default async function ProductPage({
  params,
}: {
  params: { id: string };
}) {
  let product: ProductResponse;
  let seller;
  let category = "";
  let similarProducts: ProductProps[] = [];
  let feedbacks = [];

  try {
    const productInfo = await fetchServer(
      `/api/products/visit/${params.id}`
    ).then((res) => res.json());

    product = productInfo.product;
    seller = productInfo.seller;
    category = productInfo.product.category;

    const recommendation = await fetchServer(
      `/api/products/recommendation?category=${category}`
    ).then((res) => res.json());

    similarProducts = mapProducts(recommendation[category], params.id);

    // Gets product feedbacks
    const productFeedbacks = await fetchServer(
      `/api/rating/product/${params.id}?pageNumber=0&pageSize=20`
    ).then((res) => res.json());

    feedbacks = productFeedbacks;
  } catch (error) {
    redirect("/start");
  }

  return (
    <div className={styles.containerFrame}>
      <div className={styles.content}>
        <div className={styles.productContainer}>
          {product.image ? (
            <Image
              src={product.image}
              alt="product image"
              width={712}
              height={500}
              className={styles.productImage}
            />
          ) : (
            <Skeleton variant="rounded" width={712} height={500} />
          )}
          <div className={styles.productContextArea}>
            <div className={styles.productInfoArea}>
              <Typography variant="h4">{product.name}</Typography>
              <Typography variant="h5">{`$ ${product.price}`}</Typography>
              <Rating
                readOnly
                precision={0.5}
                defaultValue={product.rating}
                size="medium"
              />
              <Typography className={styles.description} variant="body1">
                {product.description}
              </Typography>
            </div>

            <PurschaseOptions productId={params.id} />
          </div>
        </div>

        <ProductsGroup
          groupName="Similar products"
          groupProducts={similarProducts}
        />

        <div className={styles.sellerArea}>
          <SellerInfoCard
            name={seller.fullName}
            ratingValue={seller.averageRating}
            startDate={formatDateToMMDDYYYY(seller.acceptedAsSellerDate)}
          />
          <div className={styles.chatArea}>
            <div className={styles.chatOption}>
              <Typography>Questions about the product? </Typography>
              <Button disabled variant="contained" startIcon={<LiveHelp />}>
                Chat with the seller
              </Button>
            </div>

            <Divider flexItem variant="middle"/>

            <div className={styles.chatOption}>
              <Typography>Do you need support? </Typography>
              <Button disabled variant="contained" startIcon={<SupportAgent />}>
                Chat with an administrator
              </Button>
            </div>
          </div>
        </div>

        <div className={styles.feedbackContainer}>
          <Typography variant="h5">Product feedback</Typography>
          <Divider />

          <FeedbackComposition productId={params.id} />

          <div className={styles.feedback}>
            {feedbacks.length > 0 ? (
              feedbacks.map((feedback:{firstName:string, rate:number, comment:string}, index:number) => {
                return (
                  <UserFeedback
                    key={index}
                    name={feedback.firstName}
                    ratingNumber={feedback.rate}
                    description={feedback.comment}
                  />
                );
              })
            ) : (
              <>
                <Typography>The product has no reviews yet.</Typography>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

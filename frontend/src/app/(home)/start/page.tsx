import styles from "./startPage.module.css";
import CategoryCard from "@/components/cardCategory/CardCategory";
import ImageCarousel from "@/components/imageCarousel/ImageCarousel";
import ProductsGroup from "@/components/productsGroup/ProductsGroup";
import { fetchServer } from "@/services/fetchServer";

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
}

export default async function HomePage() {
  const banners = [
    {
      id: 1,
      image:
        "https://imgur.com/eaee8Oi.png",
      altText: "Banner 1",
    },
    {
      id: 2,
      image:
        "https://imgur.com/q6Qd8ix.png",
      altText: "Banner 2",
    },
    {
      id: 3,
      image: "https://imgur.com/CwplEIr.png",
      altText: "Banner 3",
    },
    {
      id: 4,
      image: "https://imgur.com/L0wMolM.png",
      altText: "Banner 4",
    },
  ];

  const categories = [
    { imageUrl: "https://imgur.com/BPIKPGc.png", title: "Cellphones" },
    { imageUrl: "https://imgur.com/cvF1v5g.png", title: "Games" },
    { imageUrl: "https://imgur.com/0OzwZX6.png", title: "School" },
    { imageUrl: "https://imgur.com/0eP0lhk.png", title: "Fashion" },
    { imageUrl: "https://imgur.com/ckLOwDK.png", title: "Accessories" },
    { imageUrl: "https://imgur.com/USTyUJ9.png", title: "Sports" },
    { imageUrl: "https://imgur.com/4RpNSGH.png", title: "Pet" },
    { imageUrl: "https://imgur.com/v3575N5.png", title: "Furniture" },
    { imageUrl: "https://imgur.com/nnEu5xn.png", title: "Household" },
    { imageUrl: "https://imgur.com/QTsrqw6.png", title: "Vehicles" },
  ];

  
  const mapProducts = (products: ProductResponse[] | undefined | null): Product[] => {
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
  
  
  const popResponse = await fetchServer("/api/products/recommendation").then((res) =>
    res.json()
  );
  const popProducts = mapProducts(popResponse)

  const cellResponse = await fetchServer("/api/products/recommendation?category=Cellphones").then((res) =>
    res.json()
  );

  const cellProducts = mapProducts(cellResponse.Cellphones)

  const gamesResponse = await fetchServer("/api/products/recommendation?category=Games").then((res) =>
    res.json()
  );
  const gamesProducts = mapProducts(gamesResponse.Games)

  const schoolResponse = await fetchServer("/api/products/recommendation?category=School").then((res) =>
    res.json()
  );
  const schollProducts = mapProducts(schoolResponse.School)

  const fashionResponse = await fetchServer("/api/products/recommendation?category=Fashion").then((res) =>
    res.json()
  );
  const fashionProducts = mapProducts(fashionResponse.Fashion)

  return (
    <div className={styles.containerFrame}>
      <ImageCarousel banners={banners} />
      
      <div className={styles.scrollContainer}>
        <div className={styles.scrollContent}>
          {categories.map((category, index) => (
            <CategoryCard
              key={index}
              imageUrl={category.imageUrl}
              title={category.title}
            />
          ))}
        </div>
      </div>
      <ProductsGroup
        groupName="Most searched products"
        groupProducts={popProducts}
      />
      <ProductsGroup groupName="Cellphones" groupProducts={cellProducts} />
      <ProductsGroup groupName="Games" groupProducts={gamesProducts} />
      <ProductsGroup groupName="School" groupProducts={schollProducts} />
      <ProductsGroup groupName="Fashion" groupProducts={fashionProducts} />
    </div>
  );
}

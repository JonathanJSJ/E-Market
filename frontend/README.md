# E-Market Front-end
This repository contains an unfinished version of a marketplace, developed with **Next.js** and **MUI**.  

⚠️ **Important Notice** ⚠️  
This repository is public for reference purposes only. **Copying, modifying, or reusing any part of this code is not allowed.**  
If you need more information, please contact me.

⚠️ **Aviso Importante** ⚠️  
Este repositório é público apenas para fins de referência. **Não é permitido copiar, modificar ou reutilizar qualquer parte deste código.**  
Se precisar de mais informações, entre em contato comigo. 

## 🚀 Installation

To install the front-end, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/JonathanJSJ/E-Market
   ```

2. Navigate to the project directory:

   ```bash
   cd frontend
   ```

3. Install dependencies:

   ```bash
   npm install
   ```


## ⚙️ Environment Configuration

Before starting the project, you need to configure the environment variables. Create a `.env` file in the project root and define the necessary variables as shown below, or use the `.env.example` file as a guide:

   ```plaintext
  NODE_ENV=dev
  PORTHTTP=3000
  PORTHTTPS=443
  NEXTAUTH_URL=http://localhost:3000
  BACKEND_HOST=http://localhost:8080
  JWT_SALT=
  GOOGLE_CLIENT_ID=
  GOOGLE_CLIENT_SECRET=
  NEXTAUTH_SECRET=
   ```


## 📦 Running the Project

After configuring the environment, you can start the project with the following command:

```bash
npm start
```

This will start the Next.js development server at `http://localhost:3000`.

## 🐳 Docker Configuration

If you prefer to run the project with Docker, follow these steps:

1. Build the Docker image:

   ```bash
   docker build -t frontend .
   ```

2. Run the Docker container using the `.env` variables:

   ```bash
   docker run -d -p 3000:3000 --env-file .env frontend
   ```


## 📖 Accessing API Documentation

The API documentation is available at the back-end URL. If the back-end is running locally, you can access it at: http://localhost:8080/swagger-ui.html

## 🛠 Technologies Used

- **Next.js** - A React framework with SSR and dynamic routing capabilities.
- **MUI (Material UI)** - A component library for UI styling.

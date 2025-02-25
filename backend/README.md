# E-Market Backend

This repository contains an unfinished version of the back-end of a marketplace, developed with **Spring Boot**.  

⚠️ **Important Notice** ⚠️  
This repository is public for reference purposes only. **Copying, modifying, or reusing any part of this code is not allowed.**  
If you need more information, please contact me.

⚠️ **Aviso Importante** ⚠️  
Este repositório é público apenas para fins de referência. **Não é permitido copiar, modificar ou reutilizar qualquer parte deste código.**  
Se precisar de mais informações, entre em contato comigo. 

## 💻 Requirements

Before starting the project, ensure you have the following technologies:

- JDK 21
- PostgreSQL or Docker

## 🚀 Installing

To install, follow these steps:

First, type in your terminal or cmd:

```bash
git clone 
```

Then, enter your username and password to clone the project.

Next, go to the project folder:

```bash
cd backend
```

## ☕ Installing Dependencies

To install the dependencies, enter the project folder and type:

- Linux:
    ```bash
    mvn clean install
    ```
- Windows:
    ```bash
    mvnw.cmd clean install
    ```

## ⚙️ Configuring the Environment

Before starting the project, you need to configure the environment variables. Follow these steps:

1. Make a copy of the `.env.example` file:
    ```bash
    cp .env.example .env
    ```

2. Open the `.env` file and adjust the environment variables as needed.

### Environment Variables

Create a `.env` file with the following content:

```env
JWT_SECRET=your_jwt_secret
DB_PASSWORD=your_db_password
DB_NAME=your_db_name
DB_USERNAME=postgres
DB_PORT=5432
DB_HOST=localhost
SERVER_HOST=localhost
SERVER_PORT=8080
```

## 📦 Docker Configuration

### Building the Docker Image

To build the Docker image, run the following command in the project root directory:

```bash
docker build -t backend .
```

### Running the Docker Container

To run the Docker container, use the following command. Make sure to replace the environment variables with your actual
values:

```bash
docker run -d -p 8080:8080 --env-file .env backend
```

#### Examples without environment  file

1. With a different database host:
    ```bash
    docker run -d -p 8080:8080 -e DB_PORT=5432 -e DB_HOST=192.168.0.50 backend
    ```
2. Change the server port:
    ```bash
    docker run -d -p 8080:8070 -e SERVER_PORT=8070 backend
    ```
3. Set the database username and password:
    ```bash
    docker run -d -p 8080:8080 -e DB_USERNAME=student -e DB_PASSWORD='Twinkle, Twinkle, Little Star' backend
    ```


## 📫 Starting the Project

After setting up the environment and starting the necessary services, you can start the project:

- Linux:
    ```bash
    mvn spring-boot:run
    ```
- Windows:
    ```bash
    mvnw.cmd spring-boot:run
    ```

## 📖 Accessing the API Documentation

Once the project is running, you can access the Swagger UI for API documentation by visiting:

http://localhost:8080/swagger-ui.html

This link is generated based on the values of `SERVER_HOST` and `SERVER_PORT` defined in your `.env` file.

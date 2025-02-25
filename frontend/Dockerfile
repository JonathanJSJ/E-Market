# Usando uma imagem base do Node.js
FROM node:18

# Definir diretório de trabalho na imagem
WORKDIR /app

# Copiar os arquivos de configuração do projeto (package.json e package-lock.json)
COPY package*.json ./

# Instalar dependências do projeto
RUN npm install

# Copiar todo o código-fonte do projeto para o diretório de trabalho
COPY . .

# Compilar o projeto
RUN npm run build

# Configurar variáveis de ambiente de desenvolvimento ou produção
ARG NODE_ENV=production
ENV NODE_ENV=${NODE_ENV}

# Expor a porta HTTP definida em .env
EXPOSE ${PORTHTTP}

# Comando para iniciar o servidor Node.js
CMD ["node", "server.js"]

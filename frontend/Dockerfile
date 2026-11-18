# Frontend (Angular + nginx) - usado pelo Coolify

# Etapa 1: compila o Angular
FROM node:24-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npx ng build

# Etapa 2: nginx entrega os arquivos e repassa /api para o backend
FROM nginx:1.27-alpine
# Arquivos em /etc/nginx/templates passam pelo envsubst ao iniciar,
# então ${BACKEND_URL} é trocado pelo valor configurado no Coolify
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80

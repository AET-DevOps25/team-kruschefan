# Stage 1: Build Angular App
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG BUILD_ENV=k8s
ENV BUILD_ENV=${BUILD_ENV}
RUN echo "Building client with profile: $BUILD_ENV"
RUN npm run build -- --configuration=$BUILD_ENV

# Stage 2: Serve with NGINX
FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/* 
COPY --from=build /app/dist/forms-ai/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

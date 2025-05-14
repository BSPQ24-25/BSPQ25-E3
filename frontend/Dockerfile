# Stage 1: Build the React frontend
FROM node:18-alpine AS build
WORKDIR /app

# Ensure legacy peer deps to avoid TypeScript/i18next conflicts
COPY package*.json ./
RUN npm install --legacy-peer-deps

# Copy sources and build
COPY . ./
RUN npm run build

# Stage 2: Serve via Nginx
FROM nginx:stable-alpine

# Copy the built static files into Nginx html folder
COPY --from=build /app/build /usr/share/nginx/html

# Expose HTTP port
EXPOSE 80

# Start Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Copy package.json and install dependencies
COPY package*.json ./
RUN npm install

# Copy source code and build the application
COPY . .
RUN npm run build -- --configuration production

# Runtime stage using Nginx
FROM nginx:alpine
# Copy the built application from the build stage to Nginx html folder
COPY --from=build /app/dist/angular-ecommerce /usr/share/nginx/html
# Copy custom nginx configuration if needed (I will create one)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

FROM node:18-alpine as build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install --registry https://registry.npmmirror.com

COPY . .
RUN chmod +x node_modules/.bin/vite && npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY --from=build /app/public/favicon.svg /usr/share/nginx/html/favicon.svg
COPY --from=build /app/public/images /usr/share/nginx/html/images
COPY --from=build /app/public/manifest.json /usr/share/nginx/html/manifest.json
COPY --from=build /app/public/sw.js /usr/share/nginx/html/sw.js
RUN mkdir -p /var/cache/nginx/images
COPY image-cache.conf /etc/nginx/conf.d/00-image-cache.conf
COPY default.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

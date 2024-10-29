FROM nginx

RUN apt-get update && apt-get -y upgrade && \
    apt-get install -y mc

WORKDIR /var/www/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
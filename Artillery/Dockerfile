FROM artilleryio/artillery:latest

ADD create-users.yml .
ADD create-auctions.yml .
ADD test-utils.js .
ADD baseworkload.yml .
ADD images ./images

RUN npm install @faker-js/faker
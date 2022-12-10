FROM artilleryio/artillery:latest

RUN npm install @faker-js/faker

WORKDIR exec

ADD create-users.yml .
ADD create-auctions.yml .
ADD test-utils.js .
ADD baseworkload.yml .
ADD images ./images
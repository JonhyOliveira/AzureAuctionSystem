FROM tomcat:10.0-jdk17-openjdk
WORKDIR /usr/local/tomcat
COPY target/scc2022-backend-1.0.war webapps/rest.war

ENV DB_LAYER_CLASS=scc.data.layers.MongoDBLayer

EXPOSE 8080
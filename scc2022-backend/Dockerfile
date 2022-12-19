FROM tomcat:10.0-jdk17-openjdk
WORKDIR /usr/local/tomcat
COPY target/scc2022-backend-1.0.war webapps/ROOT.war
# web app will be accessible through the ROOT path ('/') of the container

EXPOSE 8080
FROM openjdk:9-jdk

ADD target/scc2022-functions-1.0.0-SNAPSHOT-jar-with-dependencies.jar functions.jar

ENTRYPOINT [ "/bin/java", "-jar", "functions.jar" ]
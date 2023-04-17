# Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
# Click nbfs://nbhost/SystemFileSystem/Templates/Other/Dockerfile to edit this template

FROM adoptopenjdk/openjdk11:alpine-jre  
ARG JAR_FILE=target/table-migration-converter-1.0-SNAPSHOT.jar

ENV REMOTE_LAUNCH_HOST=srvkulan
WORKDIR /opt/app  
EXPOSE 8282:8080
COPY ${JAR_FILE} app.jar  
ENTRYPOINT ["java","-jar","app.jar"]

FROM eclipse-temurin:17-jdk-alpine as builder

MAINTAINER A.DAKWA

COPY target/restaurant-warehouse-service.jar /opt/restaurant-warehouse-service.jar

WORKDIR /opt

ENTRYPOINT ["java", "-jar", "restaurant-warehouse-service.jar"]
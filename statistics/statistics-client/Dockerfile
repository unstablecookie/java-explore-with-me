FROM amazoncorretto:11
ENV TZ="Etc/GMT-3"
COPY target/*.jar statisticsclientapp.jar
ENTRYPOINT ["java","-jar","/statisticsclientapp.jar"]
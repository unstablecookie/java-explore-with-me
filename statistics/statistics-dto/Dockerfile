FROM amazoncorretto:11
ENV TZ="Etc/GMT-3"
COPY target/*.jar statisticsdto.jar
ENTRYPOINT ["java","-jar","/statisticsdto.jar"]
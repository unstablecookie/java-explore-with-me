FROM amazoncorretto:11
ENV TZ="Etc/GMT-3"
COPY target/*.jar statisticsserverapp.jar
ENTRYPOINT ["java","-jar","/statisticsserverapp.jar"]
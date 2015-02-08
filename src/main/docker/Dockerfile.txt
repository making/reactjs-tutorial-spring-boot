FROM dockerfile/java:oracle-java8

ADD reactjs-tutorial-spring-boot.jar /opt/reactjs-tutorial-spring-boot/
EXPOSE 8080
WORKDIR /opt/reactjs-tutorial-spring-boot/
CMD ["java", "-Xms512m", "-Xmx1g", "-jar", "reactjs-tutorial-spring-boot.jar"]
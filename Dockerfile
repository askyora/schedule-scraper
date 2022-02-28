FROM askyora/base-jre:11
VOLUME /tmp
EXPOSE 8080
EXPOSE 587/tcp
ADD target/*.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
FROM openjdk:8-jre-alpine
COPY DingTalk-1.0.jar webhook.jar
ENTRYPOINT ["sh","-c","java -jar app.jar ${url} ${secret} ${atall}"]
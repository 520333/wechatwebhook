FROM openjdk:8-jre-alpine
COPY ./target/DingTalk-1.0.jar webhook.jar
ENTRYPOINT ["sh","-c","java -jar webhook.jar ${url} ${secret} ${atall}"]
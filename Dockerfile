FROM elipse-termurin:21-jdk-alpine

#로컬
EVN TZ=Asia/Seoul

RUN addgroud -S app && adduser -S app -G && \
    mkdir -p /app && chown -R app:app /app


    WORKDIR /app

ARG JAR_FILE=build/libs/*.jar

 ARG--chown=app:app ${JAR_FILE} app.jar

EXPOSE 8080

WORKDIR /app
# 도커 이미지 작성 미완성

# jdk 21 기반으로 현재 lib 밑에 build를 하여 .jar 파일을만들어서
# 이걸르 기반으로 도커 이미지 만들도록


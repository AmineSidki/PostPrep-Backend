FROM maven:3.9.12-eclipse-temurin-17-alpine

RUN mkdir /usr/app
COPY . ./usr/app/

RUN apk update
RUN apk add tesseract-ocr
RUN apk add $(apk search -q 'tesseract-ocr-data')

WORKDIR /usr/app
RUN mvn clean package -DskipTests

EXPOSE 8080

WORKDIR /usr/app/target
CMD ["java" , "-jar" ,"PostPrep-0.0.1.jar"]


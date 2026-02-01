FROM maven:3.9.12-eclipse-temurin-17-noble

RUN mkdir /usr/app
COPY . ./usr/app/

RUN apt update
RUN apt-get install tesseract-ocr -y
RUN apt-get install tesseract-ocr-all -y
RUN apt-get install libstdc++6 -y

RUN rm -rf /var/lib/apt/lists/*

WORKDIR /usr/app
RUN mvn clean package -DskipTests

EXPOSE 8080

WORKDIR /usr/app/target
CMD ["java" , "-jar" ,"PostPrep-0.0.1.jar"]


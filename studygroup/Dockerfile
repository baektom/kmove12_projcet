# 1) Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw -DskipTests package

# 2) Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# jar 복사 (studygroup 폴더 없음)
COPY --from=build /app/target/*.jar app.jar

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]

# 1) Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 프로젝트가 studygroup/ 안에 있으므로 그 폴더를 통째로 복사
COPY studygroup/ ./studygroup/

WORKDIR /app/studygroup
RUN chmod +x ./mvnw
RUN ./mvnw -DskipTests package

# 2) Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# 만들어진 jar 복사 (jar 이름 몰라도 와일드카드로 처리)
COPY --from=build /app/studygroup/target/*.jar app.jar

EXPOSE 8087
CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]

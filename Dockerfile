# Backend (Spring Boot) - usado pelo Coolify

# Etapa 1: compila o projeto e gera o .jar
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Testes pulados: o ApiApplicationTests sobe o contexto inteiro e precisa de um MySQL rodando
RUN mvn -B -DskipTests package

# Etapa 2: imagem final, só com o Java e o .jar (bem menor)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# A conexão com o banco vem das variáveis de ambiente do Coolify:
# SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME e SPRING_DATASOURCE_PASSWORD
ENTRYPOINT ["java", "-jar", "app.jar"]

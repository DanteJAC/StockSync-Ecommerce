# Etapa 1: Build del Frontend (Vue)
FROM node:20 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend .
RUN npm run build

# Etapa 2: Build del Backend (Spring Boot)
FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /app/backend

# Copiar el wrapper de maven y dependencias primero para cachear
COPY backend/.mvn .mvn
COPY backend/mvnw backend/pom.xml ./
RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copiar el código fuente
COPY backend/src ./src

# Copiar los estáticos generados por Vue a resources/static
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static

# Compilar el backend
RUN ./mvnw package -DskipTests

# Etapa 3: Imagen Final
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiar el jar generado
COPY --from=backend-build /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

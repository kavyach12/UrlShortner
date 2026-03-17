# FROM openjdk:17-jdk-slim

# WORKDIR /app

# COPY . .

# RUN chmod +x mvnw
# RUN ./mvnw clean package -DskipTests

# EXPOSE 8080

# CMD ["java", "-jar", "target/*.jar"]

# FROM eclipse-temurin:17-jdk

# WORKDIR /app

# COPY . .

# RUN chmod +x mvnw
# RUN ./mvnw clean package -DskipTests

# EXPOSE 8080

# CMD ["java", "-jar", "target/*.jar"]


# FROM eclipse-temurin:17-jdk

# WORKDIR /app

# # Copy only needed files first (better build)
# COPY pom.xml .
# COPY mvnw .
# COPY .mvn .mvn

# RUN chmod +x mvnw
# RUN ./mvnw dependency:go-offline

# # Now copy full project
# COPY src src

# RUN ./mvnw clean package -DskipTests

# EXPOSE 8080

# CMD ["sh", "-c", "java -jar target/*.jar"]


FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy everything
COPY . .

# Give permission
RUN chmod +x mvnw

# Build project
RUN ./mvnw clean package -DskipTests

# Debug: list files (important)
RUN ls -l target

EXPOSE 8080

# Run jar explicitly (no wildcard issue)
CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]
# Stage 1: Base image để chạy app
FROM eclipse-temurin:17-jdk-alpine

# Tạo thư mục làm việc
WORKDIR /app

# Copy file JAR đã build từ Jenkins sang image
COPY target/*.jar app.jar

# Expose port mặc định của Spring Boot
EXPOSE 8080

# Lệnh để khởi động ứng dụng
CMD ["java", "-jar", "app.jar"]
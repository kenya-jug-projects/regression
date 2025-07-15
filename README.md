# Regression
[![Build Status](https://github.com/kenya-jug/regression/actions/workflows/maven.yml/badge.svg)](https://github.com/kenya-jug/regression/actions/workflows/maven.yml)

A Spring Boot server for collecting, storing, and analyzing error logs with REST APIs and AI-powered log analysis

### **Regression – Requirements Specification**

![Project Overview Diagram](/specification/assets/diagram.png)

**Overview:**
**Regression** is a Spring Boot-based server for collecting, storing, and analyzing application error logs. It provides RESTful APIs for log ingestion and retrieval, with AI-driven analysis to detect anomalies, trends, and root causes.


---

<img width="1184" alt="Screenshot 2025-05-20 at 15 32 17" src="https://github.com/user-attachments/assets/b32085f6-4d35-4e35-9ad9-7dae4a27f532" />

---

https://github.com/user-attachments/assets/50589dbc-0b7f-4536-a047-e82c18b078d5

---

### Prerequisites

- Java 21 or higher
- Maven 3.8 or higher
- SQLite 3 (or your preferred database)
- Docker (optional, for containerized deployment)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/kenya-jug/regression.git
   cd regression
   ```

2. **Configure the database**
   - SQLite database will be automatically created in the project directory
   - Update `src/main/resources/application.properties` with your database configuration:
     ```properties
     spring.datasource.url=jdbc:sqlite:regression.db
     spring.datasource.driver-class-name=org.sqlite.JDBC
     ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The server will start on `http://localhost:8080/regression`

  - To access the dashboard use this credentials go to `http://127.0.0.1:8080/regression/login`
  - use the default credentials  below
  - username : `admin@regression.com`
  - password : `admin123`

### Docker Deployment

1. **Build the Docker image**
   ```bash
   docker build -t regression .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 regression
   ```

### API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Specification: `http://localhost:8080/v3/api-docs`

### Development Workflow

1. **Running Tests**
   ```bash
   mvn test
   ```

2. **Code Style Check**
   ```bash
   mvn checkstyle:check
   ```

3. **Generating Documentation**
   ```bash
   mvn javadoc:javadoc
   ```

### Contributing

1. Create a new branch for your feature
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. Make your changes and commit them
   ```bash
   git add .
   git commit -m "Description of your changes"
   ```

3. Push your changes and create a pull request




























































































<!-- coverage start -->
## 📊 Code Coverage Report

**Overall Coverage: 100.00% ✅**

| Metric      | Covered | Missed | Total | Coverage |
|-------------|---------|--------|--------|----------|
| INSTRUCTION | 3559 | 0 | 3559 | 100.00% ✅ |
| LINE | 868 | 0 | 868 | 100.00% ✅ |
| BRANCH | 90 | 0 | 90 | 100.00% ✅ |
| METHOD | 186 | 0 | 186 | 100.00% ✅ |
| CLASS | 37 | 0 | 37 | 100.00% ✅ |
| COMPLEXITY | 232 | 0 | 232 | 100.00% ✅ |
<!-- coverage end -->

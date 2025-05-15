# Student Loan Management Application (BSPQ25-E3) 

## 1. Project Description
Student Loan Management is a platform for managing student loans that allows students to apply for and manage loans to finance their education, while lenders can offer their loan products and track them. The system is designed to simplify the management of loans, ensuring that students can access the financial resources they need, while lenders can manage and oversee the status of their loans.

This project is implemented using Spring Boot, Java 17, JUnit 5 for unit testing, Mockito for mocking, and JaCoCo for test coverage. The application also utilizes JPA (Hibernate) for entity management and database handling.

## 2. Generated Documentation
- **Technical documentation (Doxygen)**: package, class and method details.  
- **Test reports**:  
  - Code coverage (JaCoCo)  
  - Unit tests (JUnit 5 + Mockito)  
  - Integration tests  
  - Performance tests (JUnitPerf)  
- **Architecture overview**: MVC diagram and explanations.  


### 3. Architecture & Folder Structure
Built with **Spring Boot** and Java 17, following MVC:

```plaintext
src/
├─ main/
│  ├─ java/com/student_loan/     ← Source code
│  │   ├─ config/                ← Application configurations
│  │   ├─ controller/            ← REST controllers
│  │   ├─ dtos/                  ← Data transfer objects
│  │   ├─ model/                 ← JPA entities
│  │   ├─ repository/            ← JPA repositories
│  │   ├─ security/              ← Security configurations
│  │   └─ service/               ← Business logic
│  └─ resources/
│      ├─ application.properties                ← Spring configuration
|      ├─ application-performance.properties    ← Performance Test configuration (optional)
|      ├─ log4j2.properties                     ← Log4J2 configuration
│      └─ dbsetup.sql                           ← MySQL setup script (optional)
└─ test/
   ├─ unit/                      ← Unit tests
   ├─ integration/               ← Integration tests
   └─ performance/               ← Performance tests
```

- **Swagger / OpenAPI**:  
  - JSON: `http://localhost:8080/api-docs`  
  - UI:    `http://localhost:8080/swagger-ui.html`  

## 4. Installation & Setup

1. **Prerequisites**  
   - Java 17+  
   - Maven  
   - Docker & Docker Compose  

2. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/StudentLoanManagement.git
   cd StudentLoanManagement
   ```

3. **Database & Application Configuration**
   Edit `src/main/resources/application.properties` (example for MySQL + Swagger + Log4j2):

   ```properties
   # MySQL (default)
   spring.datasource.url=jdbc:mysql://localhost:3306/mv
   spring.datasource.username=e3
   spring.datasource.password=e3

   # Springdoc Swagger API Config
   spring.jpa.hibernate.ddl-auto=update
   springdoc.swagger-ui.path=/swagger-ui.html
   logging.config=classpath:log4j2.xml

   # Logging (Log4j2)
   logging.config=classpath:log4j2.xml
   logging.level.org.hibernate=INFO
   logging.level.org.springframework.security=DEBUG
   logging.level.com.student_loan.security=TRACE
   ```

4. **Install Dependencies**
   ```bash
   mvn clean install
   ```

5. **Running the Application**

- **Locally via Maven**  
  ```bash
  mvn spring-boot:run
  # Or skip tests:
  mvn -DskipTests spring-boot:run
  ```
  Access at: `http://localhost:8080`

- **With Docker Compose**  
  You can launch the application and its dependencies (e.g., MySQL) using Docker:

  ```bash
  # Start containers without rebuilding
  docker-compose up

  # Rebuild images if Dockerfile or dependencies changed
  docker-compose up --build

  # Stop containers
  docker-compose down
  ```

## 5. Testing

> **Note**: Ensure MySQL is running.

#### 5.1 Unit Tests
```bash
mvn test
# Run specific tests:
mvn -Dtest=UserServiceTest,LoanServiceTest test
```

#### 5.2 Integration Tests
```bash
mvn verify -Pintegration
```

#### 5.3 Performance Tests
```bash
mvn verify -Pperformance
```

#### 5.4 Coverage (JaCoCo)
```bash
mvn clean test jacoco:report      # Generates report
mvn clean verify jacoco:report    # Enforces thresholds
```
- Report at: `target/site/jacoco/index.html`

#### 5.5 Full Site Reports
Includes JaCoCo, PMD, Checkstyle, and performance:
```bash
mvn site
mvn clean verify site
# To include performance results:
mvn verify -Pperformance
```
- Browse at: `target/site/index.html`

---

**Authors**  
Luis Rodriguez, Erkuden Camiruaga, Sabin Luja, Iker Garcia, Adam Kelly, Alex Olazabal


**This is the basic setup and usage guide for the Student Loan Management System.**
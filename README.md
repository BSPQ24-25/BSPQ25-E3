# Student Loan Management Application (BSPQ25-E3)

## Project Description
Student Loan Management is a platform for managing student loans that allows students to apply for and manage loans to finance their education, while lenders can offer their loan products and track them. The system is designed to simplify the management of loans, ensuring that students can access the financial resources they need, while lenders can manage and oversee the status of their loans.

This project is implemented using Spring Boot, Java 17, JUnit 5 for unit testing, Mockito for mocking, and JaCoCo for test coverage. The application also utilizes JPA (Hibernate) for entity management and database handling.

## Key Features
- User Management: Users can register, log in, and update their personal information.

- Item Management: Users can add, update, delete, and query items they want to lend or rent.

- Loan Management: Loans can be requested, approved, and tracked by both lenders and borrowers.

- Status Notifications: Users receive status updates on loans, items, and payments.

- Unit Tests and Mocking: Unit tests are implemented to ensure code quality, using Mockito for simulating external dependencies and JaCoCo for measuring test coverage.

## Technologies Used
- Java 17: The primary programming language used in the project.

- Spring Boot: Framework for backend application development with automatic and minimal configuration.

- Spring Data JPA: For persistence and database management using Hibernate.

- Unit 5: For running unit tests.

- Mockito: For unit testing with mock objects simulating behavior of external dependencies.

- JaCoCo: For test coverage and code analysis.

## Installation and Setup
Follow these steps to set up and run the project on your local machine:

### Prerequisites
- Java 17 or higher.

- Maven for managing dependencies and building the project.

- An IDE such as IntelliJ IDEA or Eclipse is recommended for working with the project.

#### Clone the Repository
Clone the repository to your local machine:
- git clone https://github.com/your-username/StudentLoanManagement.git

Navigate to the project folder:
- cd StudentLoanManagement

### Configure the Database
The project uses H2 Database by default for data persistence. Make sure to configure H2 or any other database you want to use in the application.properties file of Spring Boot.

If you want to use H2 as an in-memory database, you can keep the default configuration:

```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```
### Install Dependencies
Run the following command to install the necessary dependencies:

```
mvn install
```

### Running the Project
To run the project, simply use the following Maven command:

```
mvn spring-boot:run
```
This will start the server, and you can access the application at http://localhost:8080.

## Maven Commands
Here are some useful Maven commands for maintaining and managing the project:

- Clean the Project: To remove the target/ directory and any compiled files, run:
```
mvn clean
```

- Compile the Project: To compile the source code, run:
```
mvn compile
```

- Package the Project: To create a packaged .jar file:
```
mvn package
```

Install the Project: To install the project artifacts in the local Maven repository:
```
mvn install
```

### Running Tests
#### Unit Tests
The project uses JUnit 5 for unit testing. To run all the tests, use the following command:
```
mvn test
```

This will execute all unit tests and display the results in the terminal.

#### Code Coverage Tests
To generate a report of test coverage using JaCoCo, run the following command:
```
mvn verify
```

This command generates a report in the target/site/jacoco/index.html directory where you can view detailed coverage of your code.

## Authors

Luis Rodriguez, Erkuden Camiruaga, Sabin Luja, Iker Garcia, Adam Kelly, Alex Olazabal


**This concludes the basic setup and usage guide for the Student Loan Management system.**
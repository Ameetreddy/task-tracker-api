# Task Tracker API

## Overview

Task Tracker API is a RESTful backend application developed using Java and Spring Boot to manage projects, users, and tasks. It provides APIs to create, retrieve, update, and delete records while maintaining relationships between the entities.

## Technologies Used

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 In-Memory Database
- Maven
- Jakarta Bean Validation
- JUnit and Mockito
- OpenAPI 3.0

## Features

- CRUD operations for projects, users, and tasks.
- Assign tasks to projects and users.
- Validate request data using Bean Validation.
- Support pagination for listing records.
- Filter tasks by status and project ID.
- Apply default priority when task priority is not provided.
- Prevent deletion of projects that contain tasks.
- Prevent deletion of users assigned to tasks.
- Handle exceptions and return appropriate HTTP status codes.
- Document APIs using an OpenAPI YAML specification.

## Application Design

The application follows a layered architecture to separate responsibilities.

- **Controller Layer:** Handles incoming HTTP requests and returns API responses.
- **Service Layer:** Implements business logic and validates business rules.
- **Repository Layer:** Performs database operations using Spring Data JPA.
- **Entity Layer:** Defines Project, User, and Task entities and their relationships.
- **DTO Layer:** Defines the request and response data structures.
- **Exception Handling:** Handles validation errors, missing resources, duplicate records, and business rule violations.

## Data Model

The application contains three main entities.

- **Project:** Stores project details such as name and description.
- **User:** Stores user details such as name, email, and role.
- **Task:** Stores task details such as title, description, status, priority, and due date.

Each task belongs to a project and can be assigned to a user. A project can contain multiple tasks, and a user can be assigned multiple tasks.

## Prerequisites

Before running the application, install:

- JDK 21
- Maven
- IntelliJ IDEA or another Java IDE (optional)

Verify the installation using:

```bash
java -version
mvn -version
```

## How to Run

1. Open the project in IntelliJ IDEA.
2. Open a terminal in the directory containing `pom.xml`.
3. Start the application using:

```bash
mvn spring-boot:run
```

The application runs at `http://localhost:9000`.

To run the unit tests, execute:

```bash
mvn test
```

## Database Configuration

The application uses an H2 in-memory database for development and testing.

- **H2 Console:** `http://localhost:9000/h2-console`
- **JDBC URL:** `jdbc:h2:mem:taskdb`

Use the username and password configured in `application.properties`.

Since the database is in memory, the data is cleared when the application restarts.

## API Endpoints

### Projects

- **POST** `/api/projects` — Create a project.
- **GET** `/api/projects` — Retrieve projects with pagination.
- **GET** `/api/projects/{id}` — Retrieve a project by ID.
- **PUT** `/api/projects/{id}` — Update a project.
- **DELETE** `/api/projects/{id}` — Delete a project.
- **GET** `/api/projects/{id}/tasks` — Retrieve tasks belonging to a project.

### Users

- **POST** `/api/users` — Create a user.
- **GET** `/api/users` — Retrieve users with pagination.
- **GET** `/api/users/{id}` — Retrieve a user by ID.
- **PUT** `/api/users/{id}` — Update a user.
- **DELETE** `/api/users/{id}` — Delete a user.

### Tasks

- **POST** `/api/tasks` — Create a task.
- **GET** `/api/tasks` — Retrieve tasks with pagination and optional filters.
- **GET** `/api/tasks/{id}` — Retrieve a task by ID.
- **PUT** `/api/tasks/{id}` — Update a task.
- **DELETE** `/api/tasks/{id}` — Delete a task.

## Sample API Requests

The following examples assume the application is running locally on port `9000`.

### Create a Project

```bash
curl -X POST http://localhost:9000/api/projects \
  -H "Content-Type: application/json" \
  -d '{"name":"Task Tracker","description":"Manage project tasks"}'
```

### Create a User

```bash
curl -X POST http://localhost:9000/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alex","email":"alex@example.com","role":"DEVELOPER"}'
```

### Retrieve Projects

```bash
curl "http://localhost:9000/api/projects?page=0&size=10"
```

### Retrieve Users

```bash
curl "http://localhost:9000/api/users?page=0&size=10"
```

### Filter Tasks by Status

```bash
curl "http://localhost:9000/api/tasks?status=TODO&page=0&size=10"
```

### Filter Tasks by Project

```bash
curl "http://localhost:9000/api/tasks?projectId=1&page=0&size=10"
```

### Retrieve Tasks for a Project

```bash
curl http://localhost:9000/api/projects/1/tasks
```

For task creation, use an existing project ID and, if assigning a user, an existing user ID.

## Validation and Error Handling

The API validates incoming requests, including required fields, field lengths, and email formats.

The service layer checks whether referenced projects and users exist before creating or updating tasks. It also prevents deletion when the specified business rules would be violated.

The API uses appropriate HTTP status codes:

- **200 OK:** Successful retrieval or update.
- **201 Created:** Resource created successfully.
- **204 No Content:** Resource deleted successfully.
- **400 Bad Request:** Invalid request data.
- **404 Not Found:** Requested resource does not exist.
- **409 Conflict:** Duplicate records or business rule violations.

## Testing

Unit tests use JUnit and Mockito to test service-layer business logic.

Important scenarios include successful operations, missing resources, duplicate records, default task priority, and deletion restrictions.

Run the tests using:

```bash
mvn test
```

## API Documentation

The `openapi.yaml` file in the project root contains the OpenAPI 3.0 specification for the application.

It documents API endpoints, request and response structures, query parameters, and error responses. The specification can be viewed and validated using [Swagger Editor](https://editor.swagger.io/).


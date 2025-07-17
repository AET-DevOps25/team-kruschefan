# Microservices

This folder contains the backend microservices for the Forms AI platform. These services work together to enable form creation, submission, template management, and user authentication. All services are containerized and accessible through a centralized API Gateway.

## Features

- Central API Gateway for routing and aggregation
- User service integrated with Keycloak for authentication
- Template service to manage reusable form structures
- Form service to manage form creation and responses
- OpenAPI-defined endpoints for each service
- Prometheus-compatible monitoring
- Docker-ready for full local deployment

## Docker Deployment

Each service includes a Dockerfile and is orchestrated with Docker Compose.

### Quick Start

```
docker-compose up --build
```

### Common Operations

- Start in detached mode:
  ```
  docker-compose up -d
  ```
- Rebuild specific service:
  ```
  docker-compose up --build -d <service_name>
  ```
- View logs:
  ```
  docker-compose logs -f
  ```

## API Usage

### Base URL

All endpoints are accessible through:
`http://localhost:8080`

### User Service

`GET /user` — Retrieve all users  
`POST /user` — Create new user  
`GET /user/{username}` — Get user  
`PUT /user/{username}` — Update user  
`DELETE /user/{username}` — Delete user

### Template Service

`POST /template` — Create template  
`GET /template` — List templates  
`GET /template/{templateId}` — Get template  
`PUT /template/{templateId}` — Update template  
`DELETE /template/{templateId}` — Delete template

### Form Service

`GET /form` — List forms  
`POST /form` — Create form  
`GET /form/{formId}` — Get form  
`GET /form/responses` — List responses  
`POST /form/responses` — Submit response  
`GET /form/responses/{responseId}` — Get response

## Local Development Setup (Without Docker)

### Prerequisites

- `Java 21+ (JDK)`
- `Maven 3.9+`

### 1. Clone and Prepare

```
git clone <repository-url>
cd project-directory
```

### 3. Run Individual Services

For each service (in their respective folders):

```
cd services/user-service
mvn spring-boot:run
```

### 4. Required Services

Run these in separate terminals:
| Service | Command | Port |
|---------|---------|------|
| User Service | `mvn spring-boot:run` | 8081 |
| Form Service | `mvn spring-boot:run` | 8082 |
| Template Service | `mvn spring-boot:run` | 8083 |

### 5. Verify Setup

Check services at respective swagger URLs:

- User Service: `http://localhost:8081/swagger-ui/index.html`
- Form Service: `http://localhost:8082/swagger-ui/index.html`
- Template Service: `http://localhost:8083/swagger-ui/index.html`

### Development Tips

1. **Hot Reload**:

   ```bash
   # save changes without reload
   mvn spring-boot:run -Dspring-boot.run.fork=false
   ```

2. **Run Tests**:

   ```bash
   mvn test
   # For a single test class
   mvn test -Dtest=UserServiceTest
   ```

3. **Build Without Tests**:
   ```bash
   mvn install -DskipTests
   ```

## Testing

Run tests:

```
cd services/<service-name>
mvn test
```

Test coverage:

- REST API integration
- OpenAPI compliance
- Input validation

## Service Endpoints

- `API Gateway` — `http://localhost:8080`
- `User Service` — `http://localhost:8083`
- `Template Service` — `http://localhost:8082`
- `Form Service` — `http://localhost:8081`
- `Keycloak` — `http://localhost:9001`
- `Grafana` — `http://localhost:3001`
- `Prometheus` — `http://localhost:9090`

## Security

`Authorization: Bearer <access_token>`  
Authentication handled by Keycloak with JWT tokens.

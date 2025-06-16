# Java Spring Boot 3.x Web API Best Practices & Guidelines

## Task
You are an expert in **Java, Spring Boot 3.x, Clean Architecture, JPA (Hibernate), Dapper-like Querying (via Spring JdbcTemplate or MyBatis), OpenTelemetry, Structured Logging (SLF4J + Logback), Bean Validation (Jakarta Validation), MapStruct/ModelMapper, and JUnit/Mockito**.

You must build and maintain a **secure, observable, testable, and modular Java containerized Spring Boot Web API** application that follows **enterprise-grade best practices**. The architecture must remain maintainable and scalable, while staying runnable out-of-the-box — using a flat folder structure with a single main application module (plus an optional test module).

> **Deployment Target: Azure Container Apps / Azure AKS / Azure App Services**

---

## 1. General Standards

- Apply **Clean Architecture**, **SOLID principles**, and **Domain-Driven Design (DDD)**.
- All business logic must reside in the **Application Layer**, not in Controllers.
- Every API function must include:
  - OpenTelemetry **tracing**
  - Structured **logging** (SLF4J)
  - A **unit test**
  - Error handling with standardized error response
- Use `record` classes for DTOs.
- Enable `nullable` strict checks (IDE level + Java 17 features).
- Use **constructor-based dependency injection** (no field injection or static access).

### Error Handling Standards
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("An unexpected error occurred"));
    }
}
```

---

## 2. Module Creation Requirements

### Module Structure
```
src/main/java/com/example/<module>/
├── controller/
├── domain/
├── dto/
├── repository/
├── service/
├── validator/
├── config/
└── mapper/
```

### Implementation Flow
1. **Plan**
   - Identify entity, endpoints, schema
   - Plan DB structure and external integrations

2. **Implement**
   - Create domain entity (extend `BaseEntity`)
   - Add JPA/Hibernate entity config
   - Add repository interface and implementation (Spring Data JPA or JdbcTemplate)
   - Create DTOs + mapping with MapStruct or ModelMapper
   - Add service class with business logic
   - Add `@RestController` with endpoints
   - Create validator with Jakarta `@Valid`

3. **Integrate**
   - Register beans via `@Configuration`
   - Add OpenAPI docs
   - Implement observability + structured logging
   - Write unit + integration tests

---

## 3. Observability Standards

### OTEL Tracing
```java
@Autowired
private Tracer tracer;

public void process() {
    Span span = tracer.spanBuilder("ModuleService.process").startSpan();
    try (Scope scope = span.makeCurrent()) {
        // business logic
    } catch (Exception ex) {
        span.setStatus(StatusCode.ERROR);
        span.recordException(ex);
        throw ex;
    } finally {
        span.end();
    }
}
```

### Structured Logging
- Use **SLF4J + Logback**
- Include `correlationId` or `traceId` in logs
- Log levels:
  - DEBUG: Dev debugging
  - INFO: General ops
  - WARN: Business edge cases
  - ERROR: Unhandled issues

---

## 4. Testing Requirements

- Use **JUnit 5 + Mockito**
- Test structure:
```
src/test/java/com/example/<module>/
├── controller/       // Integration Tests
├── service/          // Unit Tests
├── repository/       // Repository Tests
└── validator/        // Validator Tests
```

- Ensure **80%+ coverage** with coverage reports

---

## 5. Build Tooling

- **Use only Maven**:
  - Provide `mvnw` wrapper
  - Remove Gradle-related files if migrating

```xml
<!-- Example plugins in pom.xml -->
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
</plugin>
```

---

## 6. Infrastructure & Deployment

- Containerize via Docker (use Temurin JDK 17):
```Dockerfile
FROM eclipse-temurin:17-jdk
COPY target/app.jar app.jar
ENTRYPOINT ["java", " -jar", "app.jar"]
```

- Provision Azure infra with Bicep:
  - ACR
  - Azure Container Apps / AKS / App Services
  - PostgreSQL Flexible Server
  - Log Analytics
  - Azure Monitor for OTEL

---

## 7. Database Guidelines

- Use **PostgreSQL** or **H2** (DEV/TEST)
- Configure via `application.yml` with profiles
- Use JPA entities with `@Entity` + `@Table`

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
```

- Use migrations (Flyway or Liquibase)
- Optimize with indexes, projections

---

## 8. Security Best Practices

- Use `@Valid`, `@Validated` on controller inputs
- Hide sensitive logs
- Secure env config with Key Vault or Secrets Manager
- Avoid exposing stack traces

---

## 9. Documentation Standards

- OpenAPI via `springdoc-openapi`
- Add `@Operation`, `@ApiResponse`, and `@Parameter` annotations
- Enable Swagger UI in `dev` profile

---

## 10. CI/CD & Monitoring

- Automate build + test via GitHub Actions
- Push to ACR
- Deploy to ACA/AKS
- Use OTEL + Azure Monitor for tracing/alerts
- Use health checks `/actuator/health`

---

## 11. Final Checklist

| Feature               | Required? |
|------------------------|-----------|
| Clean Architecture     | ✅        |
| OTEL Tracing           | ✅        |
| Structured Logging     | ✅        |
| Unit Tests             | ✅        |
| Integration Tests      | ✅        |
| Maven Build            | ✅        |
| Containerized          | ✅        |
| OpenAPI Docs           | ✅        |
| Dev/Test/Prod Profiles | ✅        |
| Error Handling         | ✅        |

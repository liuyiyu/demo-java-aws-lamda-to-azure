
Migration Plan: AWS Lambda Java to Containerized Spring Boot (Clean Architecture)

Target: Azure Container Apps, Azure AKS, Azure App Services (NOT AWS)

Should be completed in 6 steps, each with detailed instructions and cleaning folder and files after migration completed.
--------------------------------------------------------------------------------
Step 1: ASSESS & INITIALIZE SPRING BOOT PROJECT
--------------------------------------------------------------------------------
- Inventory all existing AWS Lambda functions and document:
  - Handler class
  - Trigger type (API Gateway, SQS, etc.)
  - Input/output structure
  - External dependencies (S3, DynamoDB, etc.)

- Create new Spring Boot project:
  - Use https://start.spring.io with Web + Spring Boot 3.x
  - Dependencies: spring-boot-starter-web, spring-boot-starter-test, Lombok, etc.
  - Package name: com.example.migration
  - Application class: MigrationApplication.java

- Folder structure:
  src/main/java/com/example/migration/
    ├── MigrationApplication.java
    ├── domain/
    ├── application/
    ├── adapter/
    ├── controller/
    └── dto/

- Configure application.yml for environments.

--------------------------------------------------------------------------------
Step 2: DEFINE DOMAIN & APPLICATION LAYERS
--------------------------------------------------------------------------------
- Move business logic from Lambda handler to use case services.

- Domain Layer:
  - Plain Java classes (e.g., User.java)
  - Interfaces for ports (e.g., UserRepository)

- Application Layer:
  - Service classes (e.g., UserService.java)
  - Implements business orchestration logic

Example:
  class User {
    String id;
    String name;
  }

  interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
  }

  class UserService {
    private final UserRepository userRepository;
    public User createUser(User user) {
      // business logic
      return userRepository.save(user);
    }
  }

--------------------------------------------------------------------------------
Step 3: IMPLEMENT ADAPTER (INFRASTRUCTURE) LAYER
--------------------------------------------------------------------------------
- Create adapter/persistence layer:
  - Use Spring Data JPA (e.g., UserJpaRepository)
  - Or manual in-memory repository for tests

- External service access (S3, etc.) goes here.
- Annotate with @Repository or @Component.

Example:
  interface UserJpaRepository extends JpaRepository<UserEntity, String> {}
  class UserPersistenceAdapter implements UserRepository {
    // maps UserEntity <-> User
  }

--------------------------------------------------------------------------------
Step 4: DEVELOP CONTROLLER LAYER
--------------------------------------------------------------------------------
- Replace Lambda triggers with @RestController endpoints.

- Use OpenAPI annotations via springdoc-openapi-ui.

- Map request/response DTOs to domain models.

- Use proper HTTP verbs, response codes, and validation.

Example:
  @RestController
  @RequestMapping("/users")
  class UserController {
    @PostMapping
    ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
      User user = service.createUser(request.toUser());
      return ResponseEntity.ok(new UserResponse(user));
    }
  }

--------------------------------------------------------------------------------
Step 5: INCREMENTAL MIGRATION & VERIFICATION
--------------------------------------------------------------------------------
- Migrate Lambdas function-by-function:
  - Extract logic → domain/application layer
  - Create matching controller
  - Write unit + integration tests
  - Compare behavior against Lambda (input/output)

- Add error handling:
  - Use @ControllerAdvice for global exception handler

- Add validation:
  - Use @Valid with javax.validation annotations

- Add structured logging:
  - Use SLF4J (Logback or Log4j2)

- Test in parallel with Lambda if needed.

--------------------------------------------------------------------------------
Step 6: CONTAINERIZATION & AZURE DEPLOYMENT
--------------------------------------------------------------------------------
- Create Dockerfile:
  FROM eclipse-temurin:17-jdk
  COPY target/app.jar app.jar
  ENTRYPOINT ["java", "-jar", "app.jar"]

- Build & push to Azure Container Registry (ACR):
  az acr build --image springboot-api:1.0.0 --registry myacr --file Dockerfile .

- Create ACA environment (if not yet):
  az containerapp env create --name aca-env --resource-group rg-springboot --location eastasia

- Deploy with YAML:

springboot-containerapp.yaml:
  name: springboot-api
  type: Microsoft.App/containerApps@2023-05-01
  location: eastasia
  properties:
    environmentId: /subscriptions/<subscription-id>/resourceGroups/rg-springboot/providers/Microsoft.App/managedEnvironments/aca-env
    configuration:
      ingress:
        external: true
        targetPort: 8080
        transport: auto
        allowInsecure: false
      registries:
        - server: myacr.azurecr.io
          identity: system
      secrets:
        - name: spring_profile
          value: "prod"
    template:
      containers:
        - name: springboot-api
          image: myacr.azurecr.io/springboot-api:1.0.0
          resources:
            cpu: 0.5
            memory: 1Gi
          env:
            - name: SPRING_PROFILES_ACTIVE
              secretRef: spring_profile
      scale:
        minReplicas: 1
        maxReplicas: 5
        rules:
          - name: http-scaling
            http:
              metadata:
                concurrentRequests: "100"

- Deploy using:
  az containerapp create --resource-group rg-springboot --file springboot-containerapp.yaml

--------------------------------------------------------------------------------
BEST PRACTICES
--------------------------------------------------------------------------------
- Keep domain layer framework-free.
- Use constructor-based dependency injection.
- Use DTOs to isolate API contracts.
- Enable OpenAPI docs for client clarity.
- Avoid mixing responsibilities across layers.
- Follow clean logging (no sensitive data).
- Write unit and integration tests per function.
- Use environment profiles for config separation.
- Containerize with slim images (multi-stage builds).

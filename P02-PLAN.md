# AWS Lambda to Azure Function Migration Plan

## Phase 1: Project Setup and Configuration
**Estimated Time: 2-3 hours**

### 1.1 Project Structure Setup
- [ ] Create new Azure Functions project structure
- [ ] Create `host.json` configuration file
- [ ] Create `local.settings.json` for local development
- [ ] Remove AWS-specific files:
  - `template.yml`
  - `build.gradle`
  - `src/assembly/bin.xml`

### 1.2 Maven Configuration Update
- [ ] Update `pom.xml`:
  - Remove AWS dependencies
  - Add Azure Functions dependencies:
    ```xml
    <dependency>
        <groupId>com.microsoft.azure.functions</groupId>
        <artifactId>azure-functions-java-library</artifactId>
        <version>3.0.0</version>
    </dependency>
    ```
  - Add Azure Functions Maven plugin:
    ```xml
    <plugin>
        <groupId>com.microsoft.azure</groupId>
        <artifactId>azure-functions-maven-plugin</artifactId>
        <version>1.28.0</version>
    </plugin>
    ```

## Phase 2: Code Migration
**Estimated Time: 4-5 hours**

### 2.1 Entry Point Migration
- [ ] Remove `StreamLambdaHandler.java`
- [ ] Update `Application.java` for Azure Functions
- [ ] Create function configuration class

### 2.2 Controller Migration
- [ ] Convert `PingController` to Azure Function:
  - Create `PingFunction.java`
  - Implement HTTP trigger
  - Add Function annotations

- [ ] Convert `CourseController` to Azure Functions:
  - Create `CourseFunctions.java`
  - Implement HTTP triggers for:
    - List courses (GET)
    - Get course by ID (GET)
    - Create course (POST)
  - Add Function annotations
  - Update request/response handling

### 2.3 Service Layer Updates
- [ ] Review and update `CourseService.java`:
  - Remove AWS-specific code if any
  - Update dependency injection
  - Update error handling

### 2.4 DTO Updates
- [ ] Review and update `Course.java`:
  - Add Azure Function specific serialization if needed
  - Update validation annotations

## Phase 3: Configuration and Environment Setup
**Estimated Time: 2-3 hours**

### 3.1 Azure Functions Configuration
- [ ] Create `host.json`:
  ```json
  {
    "version": "2.0",
    "logging": {
      "applicationInsights": {
        "samplingSettings": {
          "isEnabled": true,
          "excludedTypes": "Request"
        }
      }
    }
  }
  ```

### 3.2 Local Development Setup
- [ ] Create `local.settings.json`:
  ```json
  {
    "IsEncrypted": false,
    "Values": {
      "AzureWebJobsStorage": "UseDevelopmentStorage=true",
      "FUNCTIONS_WORKER_RUNTIME": "java"
    }
  }
  ```

### 3.3 Application Settings
- [ ] Configure environment variables
- [ ] Set up connection strings if needed
- [ ] Configure CORS settings

## Phase 4: Testing and Validation
**Estimated Time: 3-4 hours**

### 4.1 Unit Tests Update
- [ ] Update `StreamLambdaHandlerTest.java` to new Azure Function tests
- [ ] Create new test classes for each Function
- [ ] Update test dependencies in `pom.xml`
- [ ] Implement integration tests for Azure Functions

### 4.2 Local Testing
- [ ] Test all endpoints locally using Azure Functions Core Tools
- [ ] Validate request/response patterns
- [ ] Test error handling
- [ ] Performance testing

## Phase 5: Deployment and CI/CD
**Estimated Time: 2-3 hours**

### 5.1 Azure Resources Setup
- [ ] Create Azure Function App
- [ ] Configure App Service Plan
- [ ] Set up Application Insights

### 5.2 CI/CD Pipeline
- [ ] Create GitHub Actions workflow:
  ```yaml
  name: Build and deploy
  on:
    push:
      branches: [ main ]
  jobs:
    build-and-deploy:
      runs-on: ubuntu-latest
      steps:
        - uses: actions/checkout@v3
        - name: Setup Java
          uses: actions/setup-java@v3
          with:
            distribution: 'temurin'
            java-version: '17'
        - name: Build with Maven
          run: mvn clean package
        - name: Deploy to Azure
          uses: Azure/functions-action@v1
          with:
            app-name: your-function-app-name
            package: ${{ github.workspace }}/target/azure-functions/
            publish-profile: ${{ secrets.AZURE_FUNCTIONAPP_PUBLISH_PROFILE }}
  ```

### 5.3 Monitoring Setup
- [ ] Configure Application Insights
- [ ] Set up alerts
- [ ] Configure logging

## Phase 6: Documentation and Handover
**Estimated Time: 2 hours**

### 6.1 Documentation Updates
- [ ] Update README.md with Azure Functions information
- [ ] Document deployment process
- [ ] Document local development setup
- [ ] Create API documentation

### 6.2 Final Validation
- [ ] Perform end-to-end testing
- [ ] Validate all functionalities
- [ ] Performance comparison with Lambda

## Timeline Summary
- Total Estimated Time: 15-20 hours
- Recommended Timeline: 3-4 days
- Parallel Tasks: Phase 1 & 2 can be done in parallel
- Critical Path: Phase 1 → Phase 2 → Phase 4 → Phase 5

## Risk Mitigation
1. **Cold Start Performance**
   - Plan: Implement Azure Functions Premium Plan if needed
   - Fallback: Optimize code and dependencies

2. **Data Migration**
   - Plan: Validate all data patterns work similarly
   - Fallback: Create data transformation layer

3. **Integration Issues**
   - Plan: Early testing with all integrated services
   - Fallback: Create service abstractions

## Success Criteria
1. All endpoints working as in Lambda
2. Response times within acceptable range
3. Successful CI/CD deployment
4. All tests passing
5. Monitoring and logging in place
6. Documentation updated and accurate

## Rollback Plan
1. Keep Lambda deployment active during migration
2. Document all changes for reverse migration
3. Maintain configuration backups
4. Keep old AWS credentials until successful migration

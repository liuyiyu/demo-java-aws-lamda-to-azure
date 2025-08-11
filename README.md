# 🚀 AWS Lambda to Azure Container Apps Migration Journey

**Showcase Project: Migrating Spring Boot Lambda to Containerized Application with GitHub Copilot**

This repository demonstrates a complete migration strategy from AWS Lambda Java functions to containerized Spring Boot applications hosted on Azure. The project showcases how GitHub Copilot coding agents can accelerate cloud-to-cloud migration with intelligent code assistance and automated refactoring.

## 📋 Migration Overview

### Current State (AWS Lambda)
- **Runtime**: Java 21 on AWS Lambda
- **Framework**: Spring Boot 3.2.6 with `aws-serverless-java-container`
- **Trigger**: API Gateway proxy integration
- **Handler**: `StreamLambdaHandler` implementing `RequestStreamHandler`
- **Endpoints**: 
  - `GET /ping` - Health check endpoint
  - `GET /courses` - Course management API
  - `POST /courses` - Create new course
  - `PUT /courses/{id}` - Update course
  - `DELETE /courses/{id}` - Delete course

### Target State (Azure Container Apps)
- **Platform**: Azure Container Apps with Clean Architecture
- **Framework**: Spring Boot 3.x with native REST controllers
- **Infrastructure**: Bicep templates for IaC
- **Monitoring**: Azure Application Insights + Log Analytics
- **Data**: Azure PostgreSQL Flexible Server
- **CI/CD**: GitHub Actions with automated deployment

---

## 🎯 GitHub Copilot Agent Workflow

This project demonstrates how GitHub Copilot coding agents can be assigned specific migration tasks through structured prompts and instructions.

### 🤖 Agent Assignment Strategy

#### **Issue #1: Assessment & Planning Agent**
```markdown
@github-copilot assess the current AWS Lambda Spring Boot project and create a migration plan to Azure Container Apps.

Tasks:
- Inventory all Lambda functions and dependencies
- Analyze the current architecture patterns
- Create step-by-step migration roadmap
- Identify potential risks and mitigation strategies
```

#### **Issue #2: Clean Architecture Refactoring Agent**
```markdown
@github-copilot refactor the current Spring Boot Lambda code to follow Clean Architecture principles.

Tasks:
- Extract business logic from Lambda handlers
- Create domain, application, and adapter layers
- Implement dependency inversion
- Remove AWS-specific dependencies
```

#### **Issue #3: Containerization Agent**
```markdown
@github-copilot containerize the Spring Boot application for Azure deployment.

Tasks:
- Create optimized Dockerfile with multi-stage builds
- Configure application.yml for different environments
- Set up health checks and observability
- Optimize for container startup time
```

#### **Issue #4: Infrastructure as Code Agent**
```markdown
@github-copilot create Azure infrastructure using Bicep templates.

Tasks:
- Design Azure Container Apps environment
- Configure Azure Container Registry
- Set up PostgreSQL Flexible Server
- Implement Log Analytics workspace
- Create environment-specific configurations
```

---

## 🏗️ Migration Architecture

### Before: AWS Lambda Architecture
```
API Gateway → Lambda Function → In-Memory Data
     ↓
StreamLambdaHandler
     ↓
Spring Boot Controllers
```

### After: Azure Container Apps Architecture
```
Azure Front Door → Container Apps → PostgreSQL
     ↓                    ↓
Load Balancer    Application Insights
     ↓                    ↓
Clean Architecture   Log Analytics
```

---

## 📁 Project Structure Transformation

### Current Structure (Lambda-based)
```
src/main/java/com/javatechie/
├── Application.java (Lambda entry point)
├── StreamLambdaHandler.java (AWS handler)
├── controller/
│   ├── PingController.java
│   └── CourseController.java
├── dto/
│   └── Course.java
└── service/
    └── CourseService.java
```

### Target Structure (Clean Architecture)
```
src/main/java/com/example/migration/
├── MigrationApplication.java
├── domain/
│   ├── model/
│   │   └── Course.java
│   └── port/
│       └── CourseRepository.java
├── application/
│   └── service/
│       └── CourseService.java
├── adapter/
│   ├── persistence/
│   │   └── CourseJpaRepository.java
│   └── web/
│       └── CourseController.java
└── infrastructure/
    └── config/
        └── DatabaseConfig.java
```

---

## 🛠️ Migration Steps with Copilot Integration

### Step 1: Project Assessment
**Copilot Command**: 
```bash
@github-copilot analyze this AWS Lambda project structure and identify migration requirements
```

**Current Inventory**:
- ✅ 2 REST controllers (Ping, Course)
- ✅ 1 service layer (CourseService)
- ✅ 1 DTO (Course)
- ✅ AWS Lambda handler integration
- ⚠️ In-memory data storage
- ⚠️ AWS-specific dependencies

### Step 2: Clean Architecture Implementation
**Copilot Command**:
```bash
@github-copilot refactor this code to follow Clean Architecture with domain, application, and adapter layers
```

**Transformations**:
- Extract business logic to domain layer
- Create repository interfaces (ports)
- Implement adapter pattern for persistence
- Remove AWS Lambda dependencies

### Step 3: Containerization
**Copilot Command**:
```bash
@github-copilot create a production-ready Dockerfile and docker-compose for this Spring Boot app
```

**Deliverables**:
- Multi-stage Dockerfile
- Environment-specific configurations
- Health check endpoints
- Container optimization

### Step 4: Azure Infrastructure
**Copilot Command**:
```bash
@github-copilot generate Bicep templates for Azure Container Apps deployment with PostgreSQL
```

**Infrastructure Components**:
- Azure Container Registry (ACR)
- Container Apps Environment
- PostgreSQL Flexible Server
- Log Analytics Workspace
- Application Insights

### Step 5: CI/CD Pipeline
**Copilot Command**:
```bash
@github-copilot create GitHub Actions workflow for building and deploying to Azure Container Apps
```

### Step 6: Monitoring & Observability
**Copilot Command**:
```bash
@github-copilot add Azure Application Insights integration with distributed tracing
```

### Step 7: Post-Migration Cleanup
**Copilot Command**:
```bash
@github-copilot remove all AWS Lambda dependencies and update documentation
```

---

## 🚀 Quick Start with Copilot

### Prerequisites
- ✅ GitHub Copilot subscription
- ✅ Azure CLI installed
- ✅ Docker Desktop
- ✅ Java 17+ and Maven

### Using GitHub Copilot for Migration

1. **Clone and Analyze**:
   ```bash
   git clone <repository>
   cd aws-lambda
   # Ask Copilot to analyze the project
   @github-copilot assess this AWS Lambda project for Azure migration
   ```

2. **Start Migration with Copilot**:
   ```bash
   # Let Copilot create the migration plan
   @github-copilot create a step-by-step migration plan from AWS Lambda to Azure Container Apps
   ```

3. **Execute Each Step**:
   ```bash
   # Domain layer refactoring
   @github-copilot extract business logic to domain layer following Clean Architecture
   
   # Containerization
   @github-copilot create Dockerfile for Spring Boot Azure deployment
   
   # Infrastructure
   @github-copilot generate Bicep templates for Azure Container Apps
   ```

### Manual Execution (Current Lambda State)

**Build and Test Current Lambda**:
```bash
# Build the project
mvn clean package

# Test locally with SAM CLI
sam local start-api

# Test endpoint
curl http://127.0.0.1:3000/ping
```

**Deploy to AWS**:
```bash
sam build
sam deploy --guided
```

---

## 📊 Migration Progress Tracking

### Phase 1: Assessment ✅ Complete
- [x] Lambda function inventory
- [x] Dependency analysis  
- [x] Architecture assessment
- [x] Migration strategy document

### Phase 2: Refactoring 🔄 In Progress
- [ ] Domain layer extraction
- [ ] Clean Architecture implementation
- [ ] AWS dependency removal
- [ ] Unit test migration

### Phase 3: Containerization 📋 Planned
- [ ] Dockerfile creation
- [ ] Container optimization
- [ ] Environment configuration
- [ ] Health check implementation

### Phase 4: Azure Deployment 📋 Planned
- [ ] Bicep template creation
- [ ] Container Apps setup
- [ ] PostgreSQL integration
- [ ] Monitoring configuration

### Phase 5: Validation 📋 Planned
- [ ] Functional testing
- [ ] Performance comparison
- [ ] Security validation
- [ ] Documentation update

---

## 🎯 Expected Outcomes

### Performance Improvements
- **Cold Start**: Lambda ~2-3s → Container Apps ~500ms
- **Scalability**: Lambda 1000 concurrent → Container Apps auto-scale
- **Cost**: Pay-per-request → Pay-per-resource with better optimization

### Development Experience
- **Local Development**: Full Spring Boot experience
- **Debugging**: Native IDE debugging support
- **Testing**: Integration testing with actual database
- **CI/CD**: Streamlined container-based deployment

### Operational Benefits
- **Monitoring**: Rich Azure Application Insights integration
- **Logging**: Centralized Log Analytics
- **Scaling**: Horizontal and vertical scaling options
- **Maintenance**: Standard container update patterns

---

## 📚 Learning Resources

### GitHub Copilot for Migration
- [Copilot Best Practices for Large Refactoring](https://docs.github.com/copilot)
- [Using Copilot for Architecture Migrations](https://github.com/features/copilot)
- [Prompt Engineering for Code Migration](https://docs.github.com/copilot/prompts)

### Azure Container Apps
- [Container Apps Documentation](https://docs.microsoft.com/azure/container-apps/)
- [Spring Boot on Azure](https://docs.microsoft.com/azure/spring-cloud/)
- [Bicep Templates Guide](https://docs.microsoft.com/azure/azure-resource-manager/bicep/)

### Clean Architecture
- [Clean Architecture in Java](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Spring Boot Clean Architecture](https://medium.com/@ashishkumawat/spring-boot-clean-architecture-567e71b08423)

---

---

## 📊 AWS S3 Integration

This project now includes AWS S3 read functionality integrated into the CourseController. This demonstrates how to add cloud storage capabilities to your Spring Boot Lambda application.

### S3 Endpoints

#### Read S3 Object Content
```http
GET /courses/s3/read/{objectKey}
```
**Description**: Reads and returns the content of a specific S3 object.

**Response Example**:
```json
{
  "objectKey": "sample.txt",
  "content": "File content here..."
}
```

#### List S3 Objects
```http
GET /courses/s3/list
```
**Description**: Lists all objects in the configured S3 bucket.

**Response Example**:
```json
{
  "bucket": "configured-bucket",
  "objects": ["file1.txt", "file2.json", "folder/file3.pdf"]
}
```

#### Check Object Existence
```http
GET /courses/s3/exists/{objectKey}
```
**Description**: Checks if a specific object exists in the S3 bucket.

**Response Example**:
```json
{
  "objectKey": "sample.txt",
  "exists": true
}
```

### Configuration

Update your `application.properties` file:
```properties
# AWS S3 Configuration
aws.s3.bucket.name=your-bucket-name
aws.region=us-east-1
```

### AWS Credentials

For Lambda deployment, the application uses IAM roles automatically. For local development, set these environment variables:
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

### Dependencies Added

The following dependencies were added to support S3 integration:
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.69</version>
</dependency>
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>auth</artifactId>
    <version>2.20.69</version>
</dependency>
```

---

## 🤝 Contributing

This project serves as a demonstration of migration best practices with GitHub Copilot. Contributions showcasing additional Copilot usage patterns are welcome!

### How to Contribute
1. Fork the repository
2. Create a feature branch with Copilot assistance
3. Implement improvements using Copilot suggestions
4. Document your Copilot interaction patterns
5. Submit a pull request with migration insights

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🔗 Related Projects

- [Azure Spring Boot Samples](https://github.com/Azure-Samples/spring-boot-containers)
- [AWS to Azure Migration Guide](https://docs.microsoft.com/azure/architecture/aws-professional/)
- [GitHub Copilot Examples](https://github.com/copilot-tools/examples)

---

**🎉 Start your migration journey with GitHub Copilot today!**

*This README demonstrates how AI-powered coding assistants can accelerate cloud migration projects while maintaining code quality and architectural best practices.*
# demo-java-aws-lambda-s3

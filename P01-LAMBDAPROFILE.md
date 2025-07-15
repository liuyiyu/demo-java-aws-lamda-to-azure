# P01-LAMBDAPROFILE.md
# AWS Lambda to Azure Functions Migration Analysis

## Project Overview
**Project Name:** demo-java-aws-lamda-to-azure  
**Current Platform:** AWS Lambda with Spring Boot 3.2.6  
**Target Platform:** Azure Functions  
**Runtime:** Java 21  
**Migration Date:** July 15, 2025  

---

## Current Architecture Analysis

### 1. **Application Structure**
```
demo-java-aws-lamda-to-azure/
├── src/main/java/com/javatechie/
│   ├── Application.java                    # Spring Boot main class
│   ├── StreamLambdaHandler.java           # AWS Lambda handler
│   ├── controller/
│   │   ├── CourseController.java          # REST API endpoints
│   │   └── PingController.java            # Health check endpoint
│   ├── service/
│   │   ├── CourseService.java             # Business logic
│   │   └── S3Service.java                 # AWS S3 operations
│   ├── dto/
│   │   └── Course.java                    # Data model
│   └── config/
│       └── S3Config.java                  # AWS S3 configuration
├── template.yml                           # AWS SAM template
├── pom.xml                                # Maven dependencies
└── application.properties                 # Configuration
```

### 2. **Current AWS Lambda Configuration**
- **Handler:** `com.javatechie.StreamLambdaHandler::handleRequest`
- **Runtime:** Java 21
- **Memory:** 512MB
- **Timeout:** 30 seconds
- **Trigger:** API Gateway proxy integration
- **Framework:** Spring Boot with `aws-serverless-java-container`

### 3. **Key Dependencies (Current)**
```xml
<!-- AWS Lambda & Spring Boot -->
<dependency>
    <groupId>com.amazonaws.serverless</groupId>
    <artifactId>aws-serverless-java-container-springboot3</artifactId>
    <version>2.0.2</version>
</dependency>

<!-- AWS S3 SDK -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.69</version>
</dependency>

<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

## API Endpoints Analysis

### 1. **Health Check Endpoint**
- **Path:** `GET /ping`
- **Function:** Returns "Hello, World!" response
- **Migration Impact:** ✅ Direct translation to Azure Function

### 2. **Course Management Endpoints**
- **Base Path:** `/courses`
- **Operations:**
  - `POST /courses` - Create course
  - `GET /courses` - List all courses  
  - `GET /courses/{id}` - Get course by ID
  - `PUT /courses/{id}` - Update course
  - `DELETE /courses/{id}` - Delete course
- **Migration Impact:** ✅ Direct translation to Azure Function

### 3. **AWS S3 Integration Endpoints**
- **Base Path:** `/courses/s3`
- **Operations:**
  - `GET /courses/s3/read/{objectKey}` - Read S3 object content
  - `GET /courses/s3/list` - List S3 objects
  - `GET /courses/s3/exists/{objectKey}` - Check object existence
- **Migration Impact:** ⚠️ Requires Azure Blob Storage migration

---

## Migration Key Items

### 1. **Critical Components to Migrate**

#### A. **Lambda Handler → Azure Function Handler**
**Current (AWS Lambda):**
```java
public class StreamLambdaHandler implements RequestStreamHandler {
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        handler.proxyStream(inputStream, outputStream, context);
    }
}
```

**Target (Azure Functions):**
```java
public class AzureFunctionHandler {
    @FunctionName("httpTrigger")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, 
                     authLevel = AuthorizationLevel.FUNCTION) 
        HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {
        // Azure Function implementation
    }
}
```

#### B. **AWS S3 → Azure Blob Storage**
**Current (AWS S3):**
```java
@Service
public class S3Service {
    @Autowired
    private S3Client s3Client;
    
    public String readObjectFromS3(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName).key(objectKey).build();
        return s3Client.getObject(getObjectRequest);
    }
}
```

**Target (Azure Blob Storage):**
```java
@Service
public class BlobService {
    @Autowired
    private BlobServiceClient blobServiceClient;
    
    public String readBlobContent(String blobName) {
        BlobClient blobClient = blobServiceClient
                .getBlobContainerClient(containerName)
                .getBlobClient(blobName);
        return blobClient.downloadContent().toString();
    }
}
```

### 2. **Configuration Migration**

#### A. **Application Properties**
**Current (application.properties):**
```properties
# AWS S3 Configuration
aws.s3.bucket.name=your-bucket-name
aws.region=us-east-1
```

**Target (Azure Functions local.settings.json):**
```json
{
  "Values": {
    "AZURE_STORAGE_CONNECTION_STRING": "DefaultEndpointsProtocol=https;...",
    "BLOB_CONTAINER_NAME": "your-container-name"
  }
}
```

#### B. **Infrastructure as Code**
**Current (template.yml - AWS SAM):**
```yaml
AWSTemplateFormatVersion: '2010-09-09'
Transform: AWS::Serverless-2016-10-31
Resources:
  SpringBootLambdaFunction:
    Type: AWS::Serverless::Function
    Properties:
      Handler: com.javatechie.StreamLambdaHandler::handleRequest
      Runtime: java21
```

**Target (Azure Resource Manager or Bicep):**
```bicep
resource functionApp 'Microsoft.Web/sites@2021-02-01' = {
  name: functionAppName
  kind: 'functionapp,linux'
  properties: {
    serverFarmId: appServicePlan.id
    siteConfig: {
      linuxFxVersion: 'Java|21'
    }
  }
}
```

### 3. **Dependency Migration Matrix**

| AWS Component | Azure Equivalent | Migration Effort |
|---------------|------------------|------------------|
| `aws-serverless-java-container` | `azure-functions-java-library` | High |
| AWS S3 SDK | Azure Blob Storage SDK | Medium |
| AWS Lambda Runtime | Azure Functions Runtime | High |
| API Gateway | Azure Function HTTP Trigger | Medium |
| CloudFormation/SAM | ARM Templates/Bicep | Medium |

---

## Migration Strategy

### 1. **Phase 1: Core Function Migration**
- [ ] Replace `StreamLambdaHandler` with Azure Function HTTP triggers
- [ ] Update Spring Boot integration for Azure Functions
- [ ] Migrate REST controllers to Azure Function endpoints
- [ ] Update Maven dependencies

### 2. **Phase 2: Storage Migration**
- [ ] Replace AWS S3 integration with Azure Blob Storage
- [ ] Update `S3Service` to `BlobService`
- [ ] Migrate S3 configuration to Azure Storage configuration
- [ ] Update API endpoints for blob operations

### 3. **Phase 3: Infrastructure Migration**
- [ ] Create Azure Resource Manager templates
- [ ] Set up Azure Function App
- [ ] Configure Azure Storage Account
- [ ] Set up CI/CD pipelines

### 4. **Phase 4: Testing & Validation**
- [ ] Unit test migration
- [ ] Integration testing
- [ ] Performance comparison
- [ ] Load testing

---

## Risk Assessment

### **High Risk Items**
1. **Spring Boot Integration:** Azure Functions has different Spring Boot integration patterns
2. **Cold Start Performance:** May differ significantly from AWS Lambda
3. **Memory/Timeout Limits:** Azure Functions has different resource constraints

### **Medium Risk Items**
1. **S3 to Blob Storage:** API differences require code changes
2. **Configuration Management:** Different environment variable patterns
3. **Monitoring/Logging:** Different observability stack

### **Low Risk Items**
1. **Business Logic:** CourseService can be migrated with minimal changes
2. **Data Models:** Course DTO requires no changes
3. **Testing Logic:** Most unit tests can be preserved

---

## Estimated Migration Effort

| Component | Effort (Hours) | Complexity |
|-----------|----------------|------------|
| Core Function Handler | 16-24 | High |
| Storage Migration (S3→Blob) | 8-12 | Medium |
| Infrastructure Setup | 12-16 | Medium |
| Testing & Validation | 16-20 | Medium |
| Documentation | 4-6 | Low |
| **Total Estimated Effort** | **56-78 hours** | **Medium-High** |

---

## Success Criteria

### **Functional Requirements**
- [ ] All current API endpoints working in Azure Functions
- [ ] Blob storage operations equivalent to S3 operations
- [ ] Same response times and reliability

### **Non-Functional Requirements**
- [ ] Cold start time ≤ AWS Lambda performance
- [ ] Cost optimization compared to current AWS setup
- [ ] Monitoring and alerting equivalent to current setup

### **Technical Requirements**
- [ ] Java 21 runtime support
- [ ] Spring Boot 3.x compatibility
- [ ] RESTful API compliance
- [ ] Proper error handling and logging

---

## Next Steps

1. **Set up Azure Development Environment**
2. **Create Proof of Concept with single endpoint**
3. **Implement Azure Blob Storage integration**
4. **Create Azure Infrastructure templates**
5. **Plan phased migration approach**
6. **Set up monitoring and alerting**

---

*Migration Analysis completed on July 15, 2025*  
*Project: demo-java-aws-lamda-to-azure → Azure Functions*

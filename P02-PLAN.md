# P02-PLAN.md
# AWS Lambda to Azure Functions Migration Plan

## Migration Overview
**Source:** AWS Lambda Spring Boot Application  
**Target:** Azure Functions Java Application  
**Timeline:** 4 Phases, Estimated 56-78 hours  
**Plan Created:** July 15, 2025  

---

## Prerequisites & Setup

### Step 1: Development Environment Setup
**Estimated Time:** 2-3 hours

#### 1.1 Install Azure Tools
```bash
# Install Azure CLI
winget install Microsoft.AzureCLI

# Install Azure Functions Core Tools
npm install -g azure-functions-core-tools@4 --unsafe-perm true

# Install Azure Functions extension for VS Code
code --install-extension ms-azuretools.vscode-azurefunctions
```

#### 1.2 Azure Account Setup
```bash
# Login to Azure
az login

# Set subscription (if multiple)
az account set --subscription "your-subscription-id"

# Create resource group
az group create --name rg-lambda-migration --location eastus
```

#### 1.3 Create Azure Storage Account
```bash
# Create storage account for Azure Functions runtime
az storage account create \
  --name stlambdamigration \
  --resource-group rg-lambda-migration \
  --location eastus \
  --sku Standard_LRS

# Create blob storage for application data
az storage container create \
  --name course-data \
  --account-name stlambdamigration
```

---

## Phase 1: Core Function Migration (16-24 hours)

### Step 2: Create Azure Functions Project Structure
**Estimated Time:** 4-6 hours

#### 2.1 Initialize Azure Functions Project
```bash
# Create new Azure Functions project
mkdir azure-functions-migration
cd azure-functions-migration

# Initialize with Java template
func init --worker-runtime java --language java
```

#### 2.2 Update Project Structure
```
azure-functions-migration/
├── src/main/java/com/javatechie/
│   ├── AzureFunctionsApplication.java
│   ├── functions/
│   │   ├── PingFunction.java
│   │   ├── CourseFunction.java
│   │   └── BlobFunction.java
│   ├── service/
│   │   ├── CourseService.java
│   │   └── BlobService.java
│   ├── dto/
│   │   └── Course.java
│   └── config/
│       └── BlobConfig.java
├── host.json
├── local.settings.json
└── pom.xml
```

#### 2.3 Update Maven Dependencies
```xml
<!-- Replace in pom.xml -->
<dependencies>
    <!-- Azure Functions -->
    <dependency>
        <groupId>com.microsoft.azure.functions</groupId>
        <artifactId>azure-functions-java-library</artifactId>
        <version>3.0.0</version>
    </dependency>
    
    <!-- Spring Boot (modified for Azure Functions) -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-function-adapter-azure</artifactId>
        <version>4.0.0</version>
    </dependency>
    
    <!-- Azure Blob Storage -->
    <dependency>
        <groupId>com.azure</groupId>
        <artifactId>azure-storage-blob</artifactId>
        <version>12.24.0</version>
    </dependency>
    
    <!-- Jackson for JSON processing -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
</dependencies>
```

### Step 3: Migrate Application Entry Point
**Estimated Time:** 2-3 hours

#### 3.1 Create Azure Functions Application Class
```java
// File: AzureFunctionsApplication.java
package com.javatechie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.javatechie")
public class AzureFunctionsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AzureFunctionsApplication.class, args);
    }
}
```

#### 3.2 Create Function Configuration
```json
// File: host.json
{
  "version": "2.0",
  "extensionBundle": {
    "id": "Microsoft.Azure.Functions.ExtensionBundle",
    "version": "[4.*, 5.0.0)"
  },
  "functionTimeout": "00:05:00",
  "logging": {
    "logLevel": {
      "default": "Information"
    }
  }
}
```

```json
// File: local.settings.json
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "UseDevelopmentStorage=true",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "AZURE_STORAGE_CONNECTION_STRING": "DefaultEndpointsProtocol=https;AccountName=stlambdamigration;AccountKey=your-key;EndpointSuffix=core.windows.net",
    "BLOB_CONTAINER_NAME": "course-data"
  }
}
```

### Step 4: Migrate Health Check Endpoint
**Estimated Time:** 2-3 hours

#### 4.1 Create Ping Function
```java
// File: functions/PingFunction.java
package com.javatechie.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PingFunction extends FunctionInvoker<String, Map<String, String>> {

    @FunctionName("ping")
    public HttpResponseMessage ping(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.GET},
                        authLevel = AuthorizationLevel.ANONYMOUS,
                        route = "ping")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Ping function processed a request.");

        Map<String, String> response = new HashMap<>();
        response.put("pong", "Hello, World!");

        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response)
                .build();
    }
}
```

### Step 5: Migrate Course Management Functions
**Estimated Time:** 8-12 hours

#### 5.1 Create Course Functions
```java
// File: functions/CourseFunction.java
package com.javatechie.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.javatechie.dto.Course;
import com.javatechie.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import java.util.List;
import java.util.Optional;

public class CourseFunction extends FunctionInvoker<String, Object> {

    @Autowired
    private CourseService courseService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FunctionName("getAllCourses")
    public HttpResponseMessage getAllCourses(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.GET},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            List<Course> courses = courseService.getAllCourses();
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(courses)
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error getting courses: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving courses")
                    .build();
        }
    }

    @FunctionName("createCourse")
    public HttpResponseMessage createCourse(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.POST},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String requestBody = request.getBody().orElse("{}");
            Course course = objectMapper.readValue(requestBody, Course.class);
            courseService.addCourse(course);
            
            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(course)
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error creating course: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Error creating course")
                    .build();
        }
    }

    @FunctionName("getCourseById")
    public HttpResponseMessage getCourseById(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.GET},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses/{id}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String idParam = request.getQueryParameters().get("id");
            if (idParam == null) {
                // Try to get from path
                String path = request.getUri().getPath();
                String[] pathParts = path.split("/");
                idParam = pathParts[pathParts.length - 1];
            }
            
            int id = Integer.parseInt(idParam);
            Optional<Course> course = courseService.getCourseById(id);
            
            if (course.isPresent()) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(course.get())
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Course not found")
                        .build();
            }
        } catch (Exception e) {
            context.getLogger().severe("Error getting course: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid course ID")
                    .build();
        }
    }

    // Similar methods for updateCourse and deleteCourse...
}
```

---

## Phase 2: Storage Migration (8-12 hours)

### Step 6: Create Azure Blob Storage Service
**Estimated Time:** 4-6 hours

#### 6.1 Create Blob Configuration
```java
// File: config/BlobConfig.java
package com.javatechie.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlobConfig {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }
}
```

#### 6.2 Create Blob Service
```java
// File: service/BlobService.java
package com.javatechie.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlobService {

    @Autowired
    private BlobServiceClient blobServiceClient;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String readBlobContent(String blobName) {
        try {
            BlobClient blobClient = blobServiceClient
                    .getBlobContainerClient(containerName)
                    .getBlobClient(blobName);
            return blobClient.downloadContent().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error reading blob: " + e.getMessage(), e);
        }
    }

    public List<String> listBlobs() {
        try {
            BlobContainerClient containerClient = blobServiceClient
                    .getBlobContainerClient(containerName);
            return containerClient.listBlobs().stream()
                    .map(BlobItem::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error listing blobs: " + e.getMessage(), e);
        }
    }

    public boolean blobExists(String blobName) {
        try {
            BlobClient blobClient = blobServiceClient
                    .getBlobContainerClient(containerName)
                    .getBlobClient(blobName);
            return blobClient.exists();
        } catch (Exception e) {
            return false;
        }
    }
}
```

### Step 7: Create Blob Storage Functions
**Estimated Time:** 4-6 hours

#### 7.1 Create Blob Functions
```java
// File: functions/BlobFunction.java
package com.javatechie.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.javatechie.service.BlobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlobFunction extends FunctionInvoker<String, Object> {

    @Autowired
    private BlobService blobService;

    @FunctionName("readBlob")
    public HttpResponseMessage readBlob(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.GET},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses/blob/read/{blobName}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String blobName = request.getQueryParameters().get("blobName");
            if (blobName == null) {
                String path = request.getUri().getPath();
                String[] pathParts = path.split("/");
                blobName = pathParts[pathParts.length - 1];
            }

            if (!blobService.blobExists(blobName)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Blob not found: " + blobName);
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body(error)
                        .build();
            }

            String content = blobService.readBlobContent(blobName);
            Map<String, String> response = new HashMap<>();
            response.put("blobName", blobName);
            response.put("content", content);

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(response)
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error reading blob: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to read blob: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error)
                    .build();
        }
    }

    @FunctionName("listBlobs")
    public HttpResponseMessage listBlobs(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.GET},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses/blob/list")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            List<String> blobs = blobService.listBlobs();
            Map<String, Object> response = new HashMap<>();
            response.put("container", "course-data");
            response.put("blobs", blobs);

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(response)
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error listing blobs: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to list blobs: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error)
                    .build();
        }
    }

    @FunctionName("checkBlobExists")
    public HttpResponseMessage checkBlobExists(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.GET},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses/blob/exists/{blobName}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String blobName = request.getQueryParameters().get("blobName");
            if (blobName == null) {
                String path = request.getUri().getPath();
                String[] pathParts = path.split("/");
                blobName = pathParts[pathParts.length - 1];
            }

            boolean exists = blobService.blobExists(blobName);
            Map<String, Object> response = new HashMap<>();
            response.put("blobName", blobName);
            response.put("exists", exists);

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(response)
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error checking blob existence: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to check blob existence: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error)
                    .build();
        }
    }
}
```

---

## Phase 3: Infrastructure Migration (12-16 hours)

### Step 8: Create Infrastructure as Code
**Estimated Time:** 6-8 hours

#### 8.1 Create Bicep Template
```bicep
// File: infrastructure/main.bicep
@description('The name of the function app that you wish to create.')
param functionAppName string = 'func-course-api-${uniqueString(resourceGroup().id)}'

@description('Storage Account type')
@allowed([
  'Standard_LRS'
  'Standard_GRS'
  'Standard_RAGRS'
])
param storageAccountType string = 'Standard_LRS'

@description('Location for all resources.')
param location string = resourceGroup().location

@description('Location for Application Insights')
param appInsightsLocation string = resourceGroup().location

var hostingPlanName = functionAppName
var applicationInsightsName = functionAppName
var storageAccountName = 'storage${uniqueString(resourceGroup().id)}'
var functionWorkerRuntime = 'java'

resource storageAccount 'Microsoft.Storage/storageAccounts@2022-05-01' = {
  name: storageAccountName
  location: location
  sku: {
    name: storageAccountType
  }
  kind: 'Storage'
  properties: {
    supportsHttpsTrafficOnly: true
    defaultToOAuthAuthentication: true
  }
}

resource blobService 'Microsoft.Storage/storageAccounts/blobServices@2022-05-01' = {
  parent: storageAccount
  name: 'default'
}

resource container 'Microsoft.Storage/storageAccounts/blobServices/containers@2022-05-01' = {
  parent: blobService
  name: 'course-data'
  properties: {
    publicAccess: 'None'
  }
}

resource hostingPlan 'Microsoft.Web/serverfarms@2021-03-01' = {
  name: hostingPlanName
  location: location
  sku: {
    name: 'Y1'
    tier: 'Dynamic'
  }
  properties: {}
}

resource applicationInsights 'Microsoft.Insights/components@2020-02-02' = {
  name: applicationInsightsName
  location: appInsightsLocation
  kind: 'web'
  properties: {
    Application_Type: 'web'
    Request_Source: 'rest'
  }
}

resource functionApp 'Microsoft.Web/sites@2021-03-01' = {
  name: functionAppName
  location: location
  kind: 'functionapp'
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    serverFarmId: hostingPlan.id
    siteConfig: {
      appSettings: [
        {
          name: 'AzureWebJobsStorage'
          value: 'DefaultEndpointsProtocol=https;AccountName=${storageAccountName};EndpointSuffix=${environment().suffixes.storage};AccountKey=${storageAccount.listKeys().keys[0].value}'
        }
        {
          name: 'WEBSITE_CONTENTAZUREFILECONNECTIONSTRING'
          value: 'DefaultEndpointsProtocol=https;AccountName=${storageAccountName};EndpointSuffix=${environment().suffixes.storage};AccountKey=${storageAccount.listKeys().keys[0].value}'
        }
        {
          name: 'WEBSITE_CONTENTSHARE'
          value: toLower(functionAppName)
        }
        {
          name: 'FUNCTIONS_EXTENSION_VERSION'
          value: '~4'
        }
        {
          name: 'WEBSITE_NODE_DEFAULT_VERSION'
          value: '~18'
        }
        {
          name: 'APPINSIGHTS_INSTRUMENTATIONKEY'
          value: applicationInsights.properties.InstrumentationKey
        }
        {
          name: 'FUNCTIONS_WORKER_RUNTIME'
          value: functionWorkerRuntime
        }
        {
          name: 'AZURE_STORAGE_CONNECTION_STRING'
          value: 'DefaultEndpointsProtocol=https;AccountName=${storageAccountName};EndpointSuffix=${environment().suffixes.storage};AccountKey=${storageAccount.listKeys().keys[0].value}'
        }
        {
          name: 'BLOB_CONTAINER_NAME'
          value: 'course-data'
        }
      ]
      ftpsState: 'FtpsOnly'
      minTlsVersion: '1.2'
      javaVersion: '21'
    }
    httpsOnly: true
  }
}

output functionAppName string = functionAppName
output storageAccountName string = storageAccountName
```

#### 8.2 Create Deployment Scripts
```bash
# File: deploy.sh
#!/bin/bash

# Variables
RESOURCE_GROUP="rg-lambda-migration"
LOCATION="eastus"
FUNCTION_APP_NAME="func-course-api-$(date +%s)"

# Create resource group
az group create --name $RESOURCE_GROUP --location $LOCATION

# Deploy infrastructure
az deployment group create \
  --resource-group $RESOURCE_GROUP \
  --template-file infrastructure/main.bicep \
  --parameters functionAppName=$FUNCTION_APP_NAME

# Build and deploy function app
mvn clean package
func azure functionapp publish $FUNCTION_APP_NAME
```

### Step 9: Configure CI/CD Pipeline
**Estimated Time:** 6-8 hours

#### 9.1 Create GitHub Actions Workflow
```yaml
# File: .github/workflows/azure-functions-deploy.yml
name: Deploy Azure Functions

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

env:
  AZURE_FUNCTIONAPP_NAME: func-course-api-prod
  AZURE_FUNCTIONAPP_PACKAGE_PATH: '.'
  JAVA_VERSION: '21'

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
    - name: 'Checkout GitHub Action'
      uses: actions/checkout@v4

    - name: Setup Java Sdk ${{ env.JAVA_VERSION }}
      uses: actions/setup-java@v4
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: 'microsoft'

    - name: 'Restore Project Dependencies Using Mvn'
      shell: bash
      run: |
        pushd './${{ env.AZURE_FUNCTIONAPP_PACKAGE_PATH }}'
        mvn clean package
        popd

    - name: 'Run Azure Functions Action'
      uses: Azure/functions-action@v1
      id: fa
      with:
        app-name: ${{ env.AZURE_FUNCTIONAPP_NAME }}
        package: '${{ env.AZURE_FUNCTIONAPP_PACKAGE_PATH }}/target/azure-functions/${{ env.AZURE_FUNCTIONAPP_NAME }}'
        publish-profile: ${{ secrets.AZURE_FUNCTIONAPP_PUBLISH_PROFILE }}
```

---

## Phase 4: Testing & Validation (16-20 hours)

### Step 10: Create Test Suite
**Estimated Time:** 8-10 hours

#### 10.1 Unit Tests
```java
// File: src/test/java/com/javatechie/functions/PingFunctionTest.java
package com.javatechie.functions;

import com.microsoft.azure.functions.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PingFunctionTest {

    @Mock
    private HttpRequestMessage<Optional<String>> request;

    @Mock
    private ExecutionContext context;

    @Test
    void testPingFunction() {
        MockitoAnnotations.openMocks(this);
        
        // Arrange
        when(request.createResponseBuilder(HttpStatus.OK))
            .thenReturn(mock(HttpResponseMessage.Builder.class));
        
        // Act
        PingFunction function = new PingFunction();
        HttpResponseMessage response = function.ping(request, context);
        
        // Assert
        verify(request, times(1)).createResponseBuilder(HttpStatus.OK);
    }
}
```

#### 10.2 Integration Tests
```java
// File: src/test/java/com/javatechie/integration/FunctionIntegrationTest.java
package com.javatechie.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "azure.storage.connection-string=UseDevelopmentStorage=true",
    "azure.storage.container-name=test-container"
})
class FunctionIntegrationTest {

    @Test
    void contextLoads() {
        // Test that Spring context loads successfully
    }
}
```

### Step 11: Performance Testing
**Estimated Time:** 4-6 hours

#### 11.1 Load Testing Script
```bash
# File: performance-test.sh
#!/bin/bash

FUNCTION_URL="https://func-course-api-prod.azurewebsites.net"
CONCURRENT_USERS=10
DURATION=60

# Install Artillery if not installed
npm install -g artillery

# Create Artillery config
cat > artillery-config.yml << EOF
config:
  target: '$FUNCTION_URL'
  phases:
    - duration: $DURATION
      arrivalRate: $CONCURRENT_USERS
scenarios:
  - name: "Test API endpoints"
    requests:
      - get:
          url: "/api/ping"
      - get:
          url: "/api/courses"
      - get:
          url: "/api/courses/blob/list"
EOF

# Run load test
artillery run artillery-config.yml
```

### Step 12: Monitoring & Validation
**Estimated Time:** 4-6 hours

#### 12.1 Application Insights Queries
```kusto
// Query for function performance
requests
| where timestamp >= ago(1h)
| summarize avg(duration), count() by name
| order by avg_duration desc

// Query for errors
exceptions
| where timestamp >= ago(1h)
| summarize count() by type, outerMessage
| order by count_ desc
```

#### 12.2 Health Check Endpoint
```bash
# Validate all endpoints are working
curl -X GET "https://func-course-api-prod.azurewebsites.net/api/ping"
curl -X GET "https://func-course-api-prod.azurewebsites.net/api/courses"
curl -X POST "https://func-course-api-prod.azurewebsites.net/api/courses" \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"Test Course","price":99.99}'
```

---

## Migration Checklist

### Pre-Migration
- [ ] Azure environment setup complete
- [ ] Development tools installed
- [ ] Resource group and storage created
- [ ] Access permissions configured

### Phase 1 - Core Migration
- [ ] Azure Functions project created
- [ ] Maven dependencies updated
- [ ] Application entry point migrated
- [ ] Ping endpoint functional
- [ ] Course management endpoints functional

### Phase 2 - Storage Migration
- [ ] Azure Blob Storage service created
- [ ] Blob configuration implemented
- [ ] All blob operations functional
- [ ] S3 endpoints replaced with blob endpoints

### Phase 3 - Infrastructure
- [ ] Bicep templates created
- [ ] Infrastructure deployed
- [ ] CI/CD pipeline configured
- [ ] Environment variables set

### Phase 4 - Testing
- [ ] Unit tests passing
- [ ] Integration tests passing
- [ ] Performance tests completed
- [ ] Monitoring configured

### Post-Migration
- [ ] Performance baseline established
- [ ] Documentation updated
- [ ] Team training completed
- [ ] AWS resources decommissioned

---

## Rollback Plan

### Emergency Rollback Steps
1. **Immediate:** Route traffic back to AWS Lambda via DNS/load balancer
2. **Short-term:** Maintain AWS infrastructure until Azure is stable
3. **Validation:** Compare metrics between platforms
4. **Decision:** After 30 days of stable operation, decommission AWS

### Risk Mitigation
- Keep AWS Lambda running in parallel for 30 days
- Implement blue-green deployment strategy
- Monitor both platforms simultaneously
- Prepare automated rollback scripts

---

*Migration Plan created on July 15, 2025*  
*Total Estimated Effort: 56-78 hours across 4 phases*

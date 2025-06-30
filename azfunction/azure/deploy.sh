#!/bin/bash

# Variables
resourceGroupName="rg-course-management"
location="eastus"
deploymentName="course-management-deployment-$(date +%Y%m%d-%H%M%S)"

# Create Resource Group
echo "Creating Resource Group..."
az group create --name $resourceGroupName --location $location

# Deploy Azure Resources using Bicep
echo "Deploying Azure Resources..."
az deployment group create \
  --name $deploymentName \
  --resource-group $resourceGroupName \
  --template-file main.bicep \
  --parameters appName="course-management-function"

# Get Function App Name
functionAppName=$(az functionapp list -g $resourceGroupName --query "[0].name" -o tsv)

# Configure Application Settings
echo "Configuring Application Settings..."
az functionapp config appsettings set \
  --name $functionAppName \
  --resource-group $resourceGroupName \
  --settings @function-app-settings.json

# Enable Application Insights
echo "Enabling Application Insights..."
az monitor app-insights component create \
  --app $functionAppName \
  --location $location \
  --resource-group $resourceGroupName \
  --application-type web

# Configure Monitoring
echo "Configuring Monitoring..."
az monitor diagnostic-settings create \
  --name "course-management-diagnostics" \
  --resource $functionAppName \
  --resource-group $resourceGroupName \
  --logs '[{"category": "FunctionAppLogs","enabled": true}]' \
  --metrics '[{"category": "AllMetrics","enabled": true}]'

# Create Alert Rules
echo "Creating Alert Rules..."
az monitor metrics alert create \
  --name "HighExecutionTime" \
  --resource-group $resourceGroupName \
  --scopes "/subscriptions/$(az account show --query id -o tsv)/resourceGroups/$resourceGroupName/providers/Microsoft.Web/sites/$functionAppName" \
  --condition "avg FunctionExecutionTime > 5000" \
  --window-size 5m \
  --evaluation-frequency 1m \
  --severity 2

echo "Deployment Complete!"

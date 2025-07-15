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

package com.javatechie;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

public class AzureSpringBootFunction extends FunctionInvoker<Message<String>, Message<String>> {

    @FunctionName("HttpApiHandler")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "{*route}")
            HttpRequestMessage<String> request,
            final ExecutionContext context) {
        
        String body = request.getBody();
        Message<String> message = new GenericMessage<>(body != null ? body : "");
        
        try {
            context.getLogger().info("Handling request for path: " + request.getUri().getPath());
            Message<String> response = handleRequest(message, context);
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(response.getPayload())
                    .build();
        } catch (Exception ex) {
            context.getLogger().severe("Error processing request: " + ex.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + ex.getMessage())
                    .build();
        }
    }
}

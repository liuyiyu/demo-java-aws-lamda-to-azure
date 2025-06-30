package com.javatechie.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

public class PingFunction extends FunctionInvoker<Void, String> {

    @FunctionName("ping")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "ping"
            ) HttpRequestMessage<Void> request,
            final ExecutionContext context) {
        
        context.getLogger().info("Processing ping request.");
        
        return request.createResponseBuilder(HttpStatus.OK)
                .body("Application is running!")
                .header("Content-Type", "text/plain")
                .build();
    }
}

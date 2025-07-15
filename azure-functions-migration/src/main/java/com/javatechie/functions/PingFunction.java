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

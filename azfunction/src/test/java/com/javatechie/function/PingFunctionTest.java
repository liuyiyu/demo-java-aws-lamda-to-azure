package com.javatechie.function;

import com.microsoft.azure.functions.*;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class PingFunctionTest {

    @Test
    void testPingFunction() {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Void> req = mock(HttpRequestMessage.class);
        final HttpResponseMessage.Builder builder = mock(HttpResponseMessage.Builder.class);
        final HttpResponseMessage responseMock = mock(HttpResponseMessage.class);

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                return builder;
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        doAnswer(new Answer<HttpResponseMessage>() {
            @Override
            public HttpResponseMessage answer(InvocationOnMock invocation) {
                return responseMock;
            }
        }).when(builder).build();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                return builder;
            }
        }).when(builder).body(any());

        // Execute
        final PingFunction function = new PingFunction();
        function.run(req, mock(ExecutionContext.class));

        // Verify
        assertEquals(builder, req.createResponseBuilder(HttpStatus.OK));
    }
}

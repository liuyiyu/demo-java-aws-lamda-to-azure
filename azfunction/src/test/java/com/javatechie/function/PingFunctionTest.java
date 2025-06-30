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
        System.setProperty("MAIN_CLASS", "com.javatechie.CourseManagementApplication");
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Void> req = mock(HttpRequestMessage.class);
        final HttpResponseMessage.Builder builder = mock(HttpResponseMessage.Builder.class);
        final HttpResponseMessage responseMock = mock(HttpResponseMessage.class);
        final ExecutionContext context = mock(ExecutionContext.class);
        java.util.logging.Logger logger = mock(java.util.logging.Logger.class);
        org.mockito.Mockito.when(context.getLogger()).thenReturn(logger);

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

        // Add this stub for .header()
        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                return builder;
            }
        }).when(builder).header(any(String.class), any(String.class));

        // Execute
        final PingFunction function = new PingFunction();
        function.run(req, context);

        // Verify
        assertEquals(builder, req.createResponseBuilder(HttpStatus.OK));
    }
}

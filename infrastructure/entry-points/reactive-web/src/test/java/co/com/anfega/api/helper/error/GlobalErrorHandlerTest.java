package co.com.anfega.api.helper.error;

import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalErrorHandlerTest {

    @Test
    void shouldRenderBusinessExceptionUsingTraceIdFromAttribute() {
        GlobalErrorHandler handler = new GlobalErrorHandler(new ObjectMapper());
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/franquicias").build());
        exchange.getAttributes().put(Constants.TRACE_ID_ATTRIBUTE, "trace-123");

        StepVerifier.create(handler.handle(exchange, new BusinessException(ErrorCode.FRANCHISE_NOT_FOUND, "Demo", "trace-123")))
                .verifyComplete();

        String body = readBody(exchange);
        assertTrue(body.contains("\"code\":\"FRQ_NOTF_0001\""));
        assertTrue(body.contains("\"traceId\":\"trace-123\""));
        assertSame(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldRenderConstraintViolationExceptionUsingHeaderTraceId() {
        GlobalErrorHandler handler = new GlobalErrorHandler(new ObjectMapper());
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/franquicias")
                .header(Constants.TRACE_ID_HEADER, "trace-456")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("El nombre es obligatorio");

        StepVerifier.create(handler.handle(exchange, new ConstraintViolationException(Set.of(violation))))
                .verifyComplete();

        String body = readBody(exchange);
        assertTrue(body.contains("\"error\":\"Bad Request\""));
        assertTrue(body.contains("El nombre es obligatorio"));
        assertTrue(body.contains("\"traceId\":\"trace-456\""));
    }

    @Test
    void shouldRenderInvalidJsonErrors() {
        GlobalErrorHandler handler = new GlobalErrorHandler(new ObjectMapper());
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/v1/productos").build());

        StepVerifier.create(handler.handle(exchange, new ServerWebInputException("invalid body")))
                .verifyComplete();

        assertTrue(readBody(exchange).contains("\"code\":\"FRQ_VAL_0002\""));
        assertSame(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldFallbackToStaticJsonWhenSerializationFails() throws JsonProcessingException {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsBytes(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new JsonProcessingException("boom") { });
        GlobalErrorHandler handler = new GlobalErrorHandler(objectMapper);
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/boom").build());

        StepVerifier.create(handler.handle(exchange, new RuntimeException("unexpected")))
                .verifyComplete();

        String body = readBody(exchange);
        assertTrue(body.contains("\"status\":500"));
        assertTrue(body.contains("\"code\":\"FRQ_INT_0001\""));
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
    }

    private String readBody(MockServerWebExchange exchange) {
        return DataBufferUtils.join(exchange.getResponse().getBody())
                .map(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .block();
    }
}

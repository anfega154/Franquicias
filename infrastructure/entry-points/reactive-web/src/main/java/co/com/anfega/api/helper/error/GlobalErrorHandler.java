package co.com.anfega.api.helper.error;

import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Order(-2)
@Slf4j
public class GlobalErrorHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        String traceId = resolveTraceId(exchange);
        ApiErrorResponse errorResponse = buildErrorResponse(exchange, ex, traceId);

        logError(ex, traceId, errorResponse);

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(errorResponse.getStatus()));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().set(Constants.TRACE_ID_HEADER, traceId);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(toJsonBytes(errorResponse))));
    }

    private ApiErrorResponse buildErrorResponse(ServerWebExchange exchange, Throwable exception, String traceId) {
        if (exception instanceof BusinessException businessException) {
            ErrorCode errorCode = businessException.getErrorCode();
            return buildResponse(
                    HttpStatus.valueOf(errorCode.getTraditionalStatusCode()),
                    errorCode.getCode(),
                    resolveErrorLabel(errorCode),
                    businessException.getMessage(),
                    exchange,
                    traceId
            );
        }

        if (exception instanceof ConstraintViolationException constraintViolationException) {
            String validationMessage = constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.VALIDATION_ERROR.getCode(),
                    Constants.VALIDATION_ERROR,
                    validationMessage,
                    exchange,
                    traceId
            );
        }

        if (exception instanceof ServerWebInputException) {
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.INVALID_JSON.getCode(),
                    Constants.VALIDATION_ERROR,
                    Constants.INVALID_JSON_MESSAGE,
                    exchange,
                    traceId
            );
        }

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_ERROR.getCode(),
                Constants.DEFAULT_ERROR,
                Constants.DEFAULT_ERROR_MESSAGE,
                exchange,
                traceId
        );
    }

    private ApiErrorResponse buildResponse(
            HttpStatus status,
            String code,
            String error,
            String message,
            ServerWebExchange exchange,
            String traceId
    ) {
        return ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(status.value())
                .error(error)
                .code(code)
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .traceId(traceId)
                .build();
    }

    private byte[] toJsonBytes(ApiErrorResponse errorResponse) {
        try {
            return objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException exception) {
            String fallback = "{\"status\":500,\"error\":\"Internal Server Error\",\"code\":\"FRQ_INT_0001\",\"message\":\"Ha ocurrido un error inesperado\"}";
            return fallback.getBytes();
        }
    }

    private String resolveTraceId(ServerWebExchange exchange) {
        Object traceId = exchange.getAttribute(Constants.TRACE_ID_ATTRIBUTE);
        if (traceId != null) {
            return traceId.toString();
        }
        String traceIdHeader = exchange.getRequest().getHeaders().getFirst(Constants.TRACE_ID_HEADER);
        return traceIdHeader != null ? traceIdHeader : UUID.randomUUID().toString();
    }

    private String resolveErrorLabel(ErrorCode errorCode) {
        return switch (errorCode) {
            case VALIDATION_ERROR, INVALID_JSON, INVALID_TRACE_ID -> Constants.VALIDATION_ERROR;
            case FRANCHISE_NOT_FOUND, BRANCH_NOT_FOUND, PRODUCT_NOT_FOUND -> Constants.RESOURCE_NOT_FOUND_ERROR;
            case PERSISTENCE_UNAVAILABLE -> Constants.SERVICE_UNAVAILABLE_ERROR;
            default -> Constants.DEFAULT_ERROR;
        };
    }

    private void logError(Throwable exception, String traceId, ApiErrorResponse errorResponse) {
        if (errorResponse.getStatus() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            log.error(Constants.LOG_UNEXPECTED_EXCEPTION, traceId, exception);
            return;
        }

        log.warn(
                Constants.LOG_EXCEPTION_RESOLVED,
                traceId,
                errorResponse.getStatus(),
                errorResponse.getCode(),
                errorResponse.getMessage()
        );
    }
}

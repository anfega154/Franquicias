package co.com.anfega.api.config;

import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class TraceIdFilter implements WebFilter {

    private static final Pattern UUID_PATTERN = Pattern.compile(Constants.UUID_REGEX);

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String traceId = resolveTraceId(exchange.getRequest().getHeaders());

        if (traceId == null || traceId.isBlank()) {
            return continueWithTraceId(exchange, chain, UUID.randomUUID().toString());
        }

        String sanitizedTraceId = traceId.replace("\"", "");
        validateTraceId(exchange, sanitizedTraceId);

        return continueWithTraceId(exchange, chain, sanitizedTraceId);
    }

    private String resolveTraceId(HttpHeaders headers) {
        String traceId = headers.getFirst(Constants.TRACE_ID_HEADER);
        return traceId != null ? traceId : headers.getFirst(Constants.TRACE_ID_COMPATIBILITY_HEADER);
    }

    private void validateTraceId(ServerWebExchange exchange, String traceId) {
        if (!UUID_PATTERN.matcher(traceId).matches()) {
            String generatedTraceId = UUID.randomUUID().toString();
            exchange.getAttributes().put(Constants.TRACE_ID_ATTRIBUTE, generatedTraceId);
            exchange.getResponse().getHeaders().set(Constants.TRACE_ID_HEADER, generatedTraceId);
            throw BusinessException.withMessage(ErrorCode.INVALID_TRACE_ID, generatedTraceId, Constants.INVALID_TRACE_ID_MESSAGE);
        }
    }

    private Mono<Void> continueWithTraceId(ServerWebExchange exchange, WebFilterChain chain, String traceId) {
        exchange.getAttributes().put(Constants.TRACE_ID_ATTRIBUTE, traceId);
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .headers(headers -> {
                    headers.set(Constants.TRACE_ID_HEADER, traceId);
                    headers.remove(Constants.TRACE_ID_COMPATIBILITY_HEADER);
                })
                .build();
        exchange.getResponse().getHeaders().set(Constants.TRACE_ID_HEADER, traceId);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}

package co.com.anfega.api.helper.api;

import co.com.anfega.model.common.constants.Constants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import jakarta.validation.Validator;

import java.util.Set;

@Slf4j
public abstract class BaseHandler {

    protected <T> Mono<ServerResponse> ok(String traceId, String message, T body) {
        log.info(Constants.LOG_RESPONSE_SUCCESS, traceId, message);
        LocalApiResponse<T> response = new LocalApiResponse<>(Constants.SUCCESS_CODE, message, traceId, body);
        return ServerResponse.ok().bodyValue(response);
    }

    protected <T> Mono<ServerResponse> ok(String traceId, String message) {
        log.info(Constants.LOG_RESPONSE_SUCCESS, traceId, message);
        LocalApiResponse<T> response = new LocalApiResponse<>(Constants.SUCCESS_CODE, message, traceId, null);
        return ServerResponse.ok().bodyValue(response);
    }

    protected <T> Mono<ServerResponse> created(String traceId, String message, T body) {
        log.info(Constants.LOG_RESPONSE_SUCCESS, traceId, message);
        LocalApiResponse<T> response = new LocalApiResponse<>(Constants.SUCCESS_CODE, message, traceId, body);
        return ServerResponse.status(HttpStatus.CREATED).bodyValue(response);
    }

    public <T> Mono<T> bodyToMonoValidated(Validator validator, ServerRequest request, Class<T> clazz) {
        return request.bodyToMono(clazz)
                .doOnNext(dto -> {
                    Set<ConstraintViolation<T>> errors = validator.validate(dto);
                    if (!errors.isEmpty()) {
                        throw new ConstraintViolationException(errors);
                    }
                });
    }

}

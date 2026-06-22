package co.com.anfega.mongo.helper;

import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoTimeoutException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@Slf4j
@Component
public class MongoResilienceExecutor {

    private final CircuitBreaker mongoCircuitBreaker;
    private final Duration operationTimeout;

    public MongoResilienceExecutor(
            CircuitBreakerRegistry circuitBreakerRegistry,
            @Value("${adapters.mongo.resilience.circuit-breaker-name:mongoDb}") String circuitBreakerName,
            @Value("${adapters.mongo.resilience.timeout-in-ms:3000}") long timeoutInMilliseconds
    ) {
        this.mongoCircuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        this.operationTimeout = Duration.ofMillis(timeoutInMilliseconds);
    }

    public <T> Mono<T> executeMono(String operationName, Supplier<Mono<T>> operationSupplier) {
        return Mono.defer(operationSupplier)
                .timeout(operationTimeout)
                .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
                .onErrorMap(throwable -> mapFailure(operationName, throwable));
    }

    public <T> Flux<T> executeFlux(String operationName, Supplier<Flux<T>> operationSupplier) {
        return Flux.defer(operationSupplier)
                .timeout(operationTimeout)
                .transformDeferred(CircuitBreakerOperator.of(mongoCircuitBreaker))
                .onErrorMap(throwable -> mapFailure(operationName, throwable));
    }

    private Throwable mapFailure(String operationName, Throwable throwable) {
        if (throwable instanceof BusinessException) {
            return throwable;
        }

        if (throwable instanceof TimeoutException) {
            log.warn(Constants.LOG_PERSISTENCE_OPERATION_TIMEOUT, operationName, throwable);
            return BusinessException.withMessage(
                    ErrorCode.PERSISTENCE_UNAVAILABLE,
                    null,
                    String.format(Constants.PERSISTENCE_TIMEOUT_MESSAGE, operationName)
            );
        }

        if (throwable instanceof CallNotPermittedException) {
            log.warn(Constants.LOG_PERSISTENCE_CIRCUIT_OPEN, operationName, throwable);
            return BusinessException.withMessage(
                    ErrorCode.PERSISTENCE_UNAVAILABLE,
                    null,
                    String.format(Constants.PERSISTENCE_TEMPORARILY_UNAVAILABLE_MESSAGE, operationName)
            );
        }

        if (isConnectivityFailure(throwable)) {
            log.error(Constants.LOG_PERSISTENCE_CONNECTIVITY_FAILURE, operationName, throwable);
            return BusinessException.withMessage(
                    ErrorCode.PERSISTENCE_UNAVAILABLE,
                    null,
                    String.format(Constants.PERSISTENCE_UNAVAILABLE_MESSAGE, operationName)
            );
        }

        log.error(Constants.LOG_PERSISTENCE_OPERATION_FAILURE, operationName, throwable);
        return BusinessException.withMessage(
                ErrorCode.DATABASE_ERROR,
                null,
                String.format(Constants.PERSISTENCE_OPERATION_FAILED_MESSAGE, operationName)
        );
    }

    private boolean isConnectivityFailure(Throwable throwable) {
        return throwable instanceof MongoTimeoutException
                || throwable instanceof MongoSocketException
                || throwable instanceof DataAccessResourceFailureException;
    }
}

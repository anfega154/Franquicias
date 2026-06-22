package co.com.anfega.mongo.helper;

import co.com.anfega.model.common.error.BusinessException;
import com.mongodb.MongoTimeoutException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

class MongoResilienceExecutorTest {

    @Test
    void shouldMapTimeoutToBusinessException() {
        MongoResilienceExecutor mongoResilienceExecutor = new MongoResilienceExecutor(
                CircuitBreakerRegistry.ofDefaults(),
                "mongoDb",
                10
        );

        StepVerifier.create(mongoResilienceExecutor.executeMono("loading franchise", () -> Mono.never()))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && throwable.getMessage().contains("tardó demasiado"))
                .verify();
    }

    @Test
    void shouldOpenCircuitBreakerAfterConfiguredFailures() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(2)
                .minimumNumberOfCalls(2)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .build();

        MongoResilienceExecutor mongoResilienceExecutor = new MongoResilienceExecutor(
                CircuitBreakerRegistry.of(circuitBreakerConfig),
                "mongoDb",
                1000
        );

        Mono<String> failingOperation = mongoResilienceExecutor.executeMono(
                "saving product",
                () -> Mono.error(new MongoTimeoutException("mongo down"))
        );

        StepVerifier.create(failingOperation)
                .expectError(BusinessException.class)
                .verify();

        StepVerifier.create(failingOperation)
                .expectError(BusinessException.class)
                .verify();

        StepVerifier.create(mongoResilienceExecutor.executeMono("saving product", () -> Mono.just("ok")))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException
                        && throwable.getMessage().contains("temporalmente"))
                .verify();
    }
}

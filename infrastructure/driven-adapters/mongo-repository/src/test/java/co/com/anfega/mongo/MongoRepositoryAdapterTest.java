package co.com.anfega.mongo;

import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.mongo.entity.FranchiseEntity;
import co.com.anfega.mongo.helper.MongoResilienceExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoRepositoryAdapterTest {

    @Mock
    private MongoDBRepository repository;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private MongoResilienceExecutor mongoResilienceExecutor;

    private MongoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        lenient().when(mongoResilienceExecutor.executeMono(anyString(), any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Supplier<Mono<Object>> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        lenient().when(mongoResilienceExecutor.executeFlux(anyString(), any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Supplier<Flux<Object>> supplier = invocation.getArgument(1);
            return supplier.get();
        });
        adapter = new MongoRepositoryAdapter(repository, mapper, mongoResilienceExecutor);
    }

    @Test
    void shouldSaveFranchise() {
        FranchiseEntity saved = franchiseEntity("fr-1", "Franquicia Uno");
        when(repository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(adapter.save(franchise(null, "Franquicia Uno")))
                .assertNext(franchise -> assertEquals("fr-1", franchise.getId()))
                .verifyComplete();
    }

    @Test
    void shouldFindFranchiseByName() {
        when(repository.findByName("Franquicia Uno")).thenReturn(Mono.just(franchiseEntity("fr-1", "Franquicia Uno")));

        StepVerifier.create(adapter.findByName("Franquicia Uno"))
                .assertNext(franchise -> assertEquals("Franquicia Uno", franchise.getName()))
                .verifyComplete();
    }

    @Test
    void shouldUpdateFranchise() {
        FranchiseEntity existing = franchiseEntity("fr-1", "Anterior");
        FranchiseEntity updated = franchiseEntity("fr-1", "Franquicia Dos");
        when(repository.findById("fr-1")).thenReturn(Mono.just(existing));
        when(repository.save(existing)).thenReturn(Mono.just(updated));

        StepVerifier.create(adapter.update(franchise("fr-1", "Franquicia Dos")))
                .assertNext(franchise -> assertEquals("Franquicia Dos", franchise.getName()))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenUpdatingMissingFranchise() {
        when(repository.findById("fr-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.update(franchise("fr-1", "Franquicia Dos")))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessException);
                    assertEquals(ErrorCode.FRANCHISE_NOT_FOUND, ((BusinessException) error).getErrorCode());
                })
                .verify();
    }

    private FranchiseEntity franchiseEntity(String id, String name) {
        FranchiseEntity entity = new FranchiseEntity();
        entity.setId(id);
        entity.setName(name);
        return entity;
    }

    private Franchise franchise(String id, String name) {
        Franchise franchise = new Franchise();
        franchise.setId(id);
        franchise.setName(name);
        return franchise;
    }
}

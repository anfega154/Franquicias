package co.com.anfega.mongo;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.mongo.entity.BranchEntity;
import co.com.anfega.mongo.helper.MongoResilienceExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchRepositoryAdapterTest {

    @Mock
    private BranchDBRepository repository;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private MongoResilienceExecutor mongoResilienceExecutor;

    private BranchRepositoryAdapter adapter;

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
        adapter = new BranchRepositoryAdapter(repository, mapper, mongoResilienceExecutor);
    }

    @Test
    void shouldSaveBranch() {
        BranchEntity saved = new BranchEntity();
        saved.setId("br-1");
        saved.setName("Sucursal Centro");
        when(repository.save(any(BranchEntity.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(adapter.save(new Branch(null, "Sucursal Centro"), "fr-1"))
                .assertNext(branch -> {
                    assertEquals("br-1", branch.getId());
                    assertEquals("Sucursal Centro", branch.getName());
                })
                .verifyComplete();

        ArgumentCaptor<BranchEntity> captor = ArgumentCaptor.forClass(BranchEntity.class);
        verify(repository).save(captor.capture());
        assertEquals("fr-1", captor.getValue().getFranchiseId());
    }

    @Test
    void shouldFindBranchByNameAndFranchiseId() {
        BranchEntity entity = new BranchEntity();
        entity.setId("br-1");
        entity.setName("Sucursal Centro");
        when(repository.findByNameAndFranchiseId("Sucursal Centro", "fr-1")).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.findByNameAndFranchiseId("Sucursal Centro", "fr-1"))
                .assertNext(branch -> assertEquals("br-1", branch.getId()))
                .verifyComplete();
    }

    @Test
    void shouldFindAllBranchesByFranchiseId() {
        BranchEntity entity = new BranchEntity();
        entity.setId("br-1");
        entity.setName("Sucursal Centro");
        when(repository.findAllByFranchiseId("fr-1")).thenReturn(Flux.just(entity));

        StepVerifier.create(adapter.findAllByFranchiseId("fr-1"))
                .assertNext(branch -> {
                    assertEquals("Sucursal Centro", branch.getName());
                    assertTrue(branch.getProducts().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void shouldUpdateBranch() {
        BranchEntity existing = new BranchEntity();
        existing.setId("br-1");
        existing.setName("Anterior");
        BranchEntity updated = new BranchEntity();
        updated.setId("br-1");
        updated.setName("Sucursal Centro");
        when(repository.findById("br-1")).thenReturn(Mono.just(existing));
        when(repository.save(existing)).thenReturn(Mono.just(updated));

        StepVerifier.create(adapter.update(new Branch("br-1", "Sucursal Centro", List.of())))
                .assertNext(branch -> assertEquals("Sucursal Centro", branch.getName()))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenBranchDoesNotExistDuringUpdate() {
        when(repository.findById("br-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.update(new Branch("br-1", "Sucursal Centro", List.of())))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessException);
                    assertEquals(ErrorCode.BRANCH_NOT_FOUND, ((BusinessException) error).getErrorCode());
                })
                .verify();
    }
}

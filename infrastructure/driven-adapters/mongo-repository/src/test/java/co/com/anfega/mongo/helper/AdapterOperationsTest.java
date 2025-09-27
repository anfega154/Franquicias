
package co.com.anfega.mongo.helper;

import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.mongo.MongoDBRepository;
import co.com.anfega.mongo.MongoRepositoryAdapter;
import co.com.anfega.mongo.entity.FranchiseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private MongoDBRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private MongoRepositoryAdapter adapter;

    private Franchise franchise;
    private FranchiseEntity franchiseEntity;
    private Flux<FranchiseEntity> franchiseEntities;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        franchise = new Franchise();
        franchiseEntity = new FranchiseEntity();
        franchiseEntities = Flux.just(franchiseEntity);

        when(objectMapper.map(franchise, FranchiseEntity.class)).thenReturn(franchiseEntity);
        when(objectMapper.map(franchiseEntity, Franchise.class)).thenReturn(franchise);

        adapter = new MongoRepositoryAdapter(repository, objectMapper);
    }

    @Test
    void testSaveAll() {
        when(repository.saveAll(any(Flux.class))).thenReturn(franchiseEntities);

        StepVerifier.create(adapter.saveAll(Flux.just(franchise)))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void testFindById() {
        when(repository.findById("key")).thenReturn(Mono.just(franchiseEntity));

        StepVerifier.create(adapter.findById("key"))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void testFindByExample() {
        when(repository.findAll(any(Example.class))).thenReturn(franchiseEntities);

        StepVerifier.create(adapter.findByExample(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(franchiseEntities);

        StepVerifier.create(adapter.findAll())
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void testDeleteById() {
        when(repository.deleteById("key")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById("key"))
                .verifyComplete();
    }
}
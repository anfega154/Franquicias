package co.com.anfega.mongo;

import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.model.product.Product;
import co.com.anfega.mongo.entity.ProductEntity;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductDBRepository repository;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private MongoResilienceExecutor mongoResilienceExecutor;

    private ProductRepositoryAdapter adapter;

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
        adapter = new ProductRepositoryAdapter(repository, mapper, mongoResilienceExecutor);
    }

    @Test
    void shouldSaveProduct() {
        ProductEntity saved = productEntity("pd-1", "Producto A", 4L);
        when(repository.save(any(ProductEntity.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(adapter.save(new Product(null, "Producto A", 4L), "br-1"))
                .assertNext(product -> assertEquals("pd-1", product.getId()))
                .verifyComplete();
    }

    @Test
    void shouldDeleteProduct() {
        when(repository.findById("pd-1")).thenReturn(Mono.just(productEntity("pd-1", "Producto A", 4L)));
        when(repository.deleteById("pd-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.delete("pd-1"))
                .verifyComplete();

        verify(repository).deleteById("pd-1");
    }

    @Test
    void shouldFailWhenDeletingMissingProduct() {
        when(repository.findById("pd-1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.delete("pd-1"))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessException);
                    assertEquals(ErrorCode.PRODUCT_NOT_FOUND, ((BusinessException) error).getErrorCode());
                })
                .verify();
    }

    @Test
    void shouldUpdateProductStock() {
        ProductEntity existing = productEntity("pd-1", "Producto A", 4L);
        ProductEntity updated = productEntity("pd-1", "Producto A", 10L);
        when(repository.findById("pd-1")).thenReturn(Mono.just(existing));
        when(repository.save(existing)).thenReturn(Mono.just(updated));

        StepVerifier.create(adapter.updateStock("pd-1", 10L))
                .assertNext(product -> assertEquals(10L, product.getStock()))
                .verifyComplete();
    }

    @Test
    void shouldFindTopProductByBranchId() {
        when(repository.findFirstByBranchIdOrderByStockDesc("br-1"))
                .thenReturn(Mono.just(productEntity("pd-1", "Producto A", 10L)));

        StepVerifier.create(adapter.findTopByBranchId("br-1"))
                .assertNext(product -> assertEquals("Producto A", product.getName()))
                .verifyComplete();
    }

    @Test
    void shouldUpdateProductName() {
        ProductEntity existing = productEntity("pd-1", "Producto A", 4L);
        ProductEntity updated = productEntity("pd-1", "Producto B", 4L);
        when(repository.findById("pd-1")).thenReturn(Mono.just(existing));
        when(repository.save(existing)).thenReturn(Mono.just(updated));

        StepVerifier.create(adapter.update(new Product("pd-1", "Producto B", 4L)))
                .assertNext(product -> assertEquals("Producto B", product.getName()))
                .verifyComplete();
    }

    private ProductEntity productEntity(String id, String name, Long stock) {
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setStock(stock);
        return entity;
    }
}

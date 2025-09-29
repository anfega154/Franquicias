package co.com.anfega.usecase.product;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import co.com.anfega.model.product.Product;
import co.com.anfega.model.product.gateways.ProductRepository;
import co.com.anfega.model.topproductperbranch.TopProductPerBranch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class ProductUseCaseTest {

    private ProductRepository productRepository;
    private BranchRepository branchRepository;
    private FranchiseRepository franchiseRepository;
    private ProductUseCase productUseCase;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        branchRepository = mock(BranchRepository.class);
        franchiseRepository = mock(FranchiseRepository.class);
        productUseCase = new ProductUseCase(productRepository, branchRepository, franchiseRepository);
    }

    // -------------------- SAVE --------------------

    @Test
    void saveShouldReturnErrorWhenFranchiseNotFound() {
        Product product = new Product();
        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.save(product, "branch", "franchise"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("La franquicia con nombre franchise no existe."))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenBranchNotFound() {
        Product product = new Product();

        Franchise franchise = new Franchise();
        franchise.setId("f1");

        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.just(franchise));
        when(branchRepository.findByNameAndFranchiseId("branch", "f1")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.save(product, "branch", "franchise"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("La sucursal con nombre branch no existe."))
                .verify();
    }

    @Test
    void saveShouldSaveProductSuccessfully() {
        Product product = new Product();
        product.setName("P1");
        product.setStock(10L);

        Franchise franchise = new Franchise();
        franchise.setId("f1");

        Branch branch = new Branch();
        branch.setId("b1");

        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.just(franchise));
        when(branchRepository.findByNameAndFranchiseId("branch", "f1")).thenReturn(Mono.just(branch));
        when(productRepository.save(product, "b1")).thenReturn(Mono.just(product));

        StepVerifier.create(productUseCase.save(product, "branch", "franchise"))
                .expectNext(product)
                .verifyComplete();

        verify(productRepository).save(product, "b1");
    }

    // -------------------- DELETE --------------------

    @Test
    void deleteShouldDeleteProductSuccessfully() {
        when(productRepository.delete("p1")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.delete("p1"))
                .verifyComplete();

        verify(productRepository).delete("p1");
    }

    // -------------------- UPDATE STOCK --------------------

    @Test
    void updateStockShouldUpdateSuccessfully() {
        Product product = new Product();
        product.setId("p1");
        product.setStock(20L);

        when(productRepository.updateStock("p1", 20L)).thenReturn(Mono.just(product));

        StepVerifier.create(productUseCase.updateStock("p1", 20L))
                .expectNext(product)
                .verifyComplete();

        verify(productRepository).updateStock("p1", 20L);
    }

    // -------------------- GET TOP PRODUCT PER BRANCH --------------------

    @Test
    void getTopShouldReturnErrorWhenFranchiseNotFound() {
        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.getTopProductPerBranch("franchise"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("La franquicia con nombre franchise no existe."))
                .verify();
    }

    @Test
    void getTopShouldReturnTopProductsSuccessfully() {
        Franchise franchise = new Franchise();
        franchise.setId("f1");

        Branch branch = new Branch();
        branch.setId("b1");
        branch.setName("Sucursal 1");

        Product product = new Product();
        product.setId("p1");
        product.setName("Prod 1");
        product.setStock(30L);

        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.just(franchise));
        when(branchRepository.findAllByFranchiseId("f1")).thenReturn(Flux.just(branch));
        when(productRepository.findTopByBranchId("b1")).thenReturn(Mono.just(product));

        StepVerifier.create(productUseCase.getTopProductPerBranch("franchise"))
                .expectNextMatches(top ->
                        top instanceof TopProductPerBranch &&
                                top.getBranchId().equals("b1") &&
                                top.getProductId().equals("p1") &&
                                top.getStock() == 30L)
                .verifyComplete();

        verify(productRepository).findTopByBranchId("b1");
    }

    // -------------------- UPDATE --------------------

    @Test
    void updateShouldUpdateSuccessfully() {
        Product product = new Product();
        product.setId("p1");
        product.setName("Prod");
        product.setStock(50L);

        when(productRepository.update(product)).thenReturn(Mono.just(product));

        StepVerifier.create(productUseCase.update(product))
                .expectNext(product)
                .verifyComplete();

        verify(productRepository).update(product);
    }

    // -------------------- ERROR  --------------------

    @Test
    void shouldReturnErrorWhenSaveFails() {
        Product product = new Product();

        Franchise franchise = new Franchise();
        franchise.setId("f1");

        Branch branch = new Branch();
        branch.setId("b1");

        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.just(franchise));
        when(branchRepository.findByNameAndFranchiseId("branch", "f1")).thenReturn(Mono.just(branch));
        when(productRepository.save(any(Product.class), eq("b1")))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productUseCase.save(product, "branch", "franchise"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("DB error"))
                .verify();

        verify(productRepository, times(1)).save(product, "b1");
    }

    @Test
    void shouldReturnErrorWhenDeleteFails() {
        when(productRepository.delete("p1"))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productUseCase.delete("p1"))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("DB error"))
                .verify();

        verify(productRepository, times(1)).delete("p1");
    }

    @Test
    void shouldReturnErrorWhenUpdateStockFails() {
        when(productRepository.updateStock("p1", 20L))
                .thenReturn(Mono.error(new RuntimeException("DB error")));
        StepVerifier.create(productUseCase.updateStock("p1", 20L))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("DB error"))
                .verify();
        verify(productRepository, times(1)).updateStock("p1", 20L);
    }
}

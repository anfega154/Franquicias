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
    void saveShouldReturnErrorWhenProductIsNull() {
        StepVerifier.create(productUseCase.save(null, "branch", "franchise"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El nombre del producto no puede estar vacío"))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenProductNameIsEmpty() {
        Product product = new Product();
        product.setName("");

        StepVerifier.create(productUseCase.save(product, "branch", "franchise"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El nombre del producto no puede estar vacío"))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenStockIsNull() {
        Product product = new Product();
        product.setName("P1");
        product.setStock(null);

        StepVerifier.create(productUseCase.save(product, "branch", "franchise"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El stock del producto no puede ser nulo o negativo"))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenStockIsNegative() {
        Product product = new Product();
        product.setName("P1");
        product.setStock(-1L);

        StepVerifier.create(productUseCase.save(product, "branch", "franchise"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El stock del producto no puede ser nulo o negativo"))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenBranchNameIsEmpty() {
        Product product = new Product();
        product.setName("P1");
        product.setStock(10L);

        StepVerifier.create(productUseCase.save(product, "", "franchise"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El nombre de la sucursal no puede estar vacío"))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenFranchiseNameIsEmpty() {
        Product product = new Product();
        product.setName("P1");
        product.setStock(10L);

        StepVerifier.create(productUseCase.save(product, "branch", ""))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El nombre de la franquicia no puede estar vacío"))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenFranchiseNotFound() {
        Product product = new Product();
        product.setName("P1");
        product.setStock(10L);

        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.save(product, "branch", "franchise"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("La franquicia con nombre franchise no existe."))
                .verify();
    }

    @Test
    void saveShouldReturnErrorWhenBranchNotFound() {
        Product product = new Product();
        product.setName("P1");
        product.setStock(10L);

        Franchise franchise = new Franchise();
        franchise.setId("f1");
        franchise.setName("franchise");

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
        product.setId("p1");
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
    }

    // -------------------- DELETE --------------------

    @Test
    void deleteShouldReturnErrorWhenProductIdIsEmpty() {
        StepVerifier.create(productUseCase.delete(""))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El ID del producto no puede estar vacío"))
                .verify();
    }

    @Test
    void deleteShouldDeleteProductSuccessfully() {
        when(productRepository.delete("p1")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.delete("p1"))
                .verifyComplete();
    }

    // -------------------- UPDATE STOCK --------------------

    @Test
    void updateStockShouldReturnErrorWhenProductIdIsEmpty() {
        StepVerifier.create(productUseCase.updateStock("", 5))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El stock del producto no puede ser nulo o negativo"))
                .verify();
    }

    @Test
    void updateStockShouldReturnErrorWhenStockIsNegative() {
        StepVerifier.create(productUseCase.updateStock("p1", -1))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El stock del producto no puede ser nulo o negativo"))
                .verify();
    }

    @Test
    void updateStockShouldUpdateSuccessfully() {
        Product product = new Product();
        product.setId("p1");
        product.setStock(20L);

        when(productRepository.updateStock("p1", 20L)).thenReturn(Mono.just(product));

        StepVerifier.create(productUseCase.updateStock("p1", 20L))
                .expectNext(product)
                .verifyComplete();
    }

    // -------------------- GET TOP PRODUCT PER BRANCH --------------------

    @Test
    void getTopShouldReturnErrorWhenFranchiseNameIsEmpty() {
        StepVerifier.create(productUseCase.getTopProductPerBranch(""))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals("El nombre de la franquicia no puede estar vacío"))
                .verify();
    }

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
    }
}

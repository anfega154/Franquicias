package co.com.anfega.usecase.product;


import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import co.com.anfega.model.product.Product;
import co.com.anfega.model.product.gateways.ProductInputPort;
import co.com.anfega.model.product.gateways.ProductRepository;
import co.com.anfega.model.topproductperbranch.TopProductPerBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProductUseCase implements ProductInputPort {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    private static final String DOES_NOT_EXIST = " no existe.";

    public ProductUseCase(ProductRepository productRepository, BranchRepository branchRepository, FranchiseRepository franchiseRepository) {
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
        this.franchiseRepository = franchiseRepository;
    }

    @Override
    public Mono<Product> save(Product product, String branchName, String franchiseName) {
        return franchiseRepository.findByName(franchiseName)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La franquicia con nombre " + franchiseName + DOES_NOT_EXIST)))
                .flatMap(franchise ->
                        branchRepository.findByNameAndFranchiseId(branchName, franchise.getId())
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("La sucursal con nombre " + branchName + DOES_NOT_EXIST)))
                                .flatMap(branch -> productRepository.save(product, branch.getId()))
                );
    }

    @Override
    public Mono<Void> delete(String productId) {
        return productRepository.delete(productId);
    }

    @Override
    public Mono<Product> updateStock(String productId, long newStock) {
        return productRepository.updateStock(productId, newStock);
    }

    @Override
    public Flux<TopProductPerBranch> getTopProductPerBranch(String franchiseName) {
        return franchiseRepository.findByName(franchiseName)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La franquicia con nombre " + franchiseName + DOES_NOT_EXIST)))
                .flatMapMany(franchise ->
                        branchRepository.findAllByFranchiseId(franchise.getId())
                                .flatMap(branch ->
                                        productRepository.findTopByBranchId(branch.getId())
                                                .map(product -> new TopProductPerBranch(
                                                        branch.getId(),
                                                        branch.getName(),
                                                        product.getId(),
                                                        product.getName(),
                                                        product.getStock()
                                                ))
                                )
                );
    }

    @Override
    public Mono<Product> update(Product product) {
        return productRepository.update(product);
    }
}

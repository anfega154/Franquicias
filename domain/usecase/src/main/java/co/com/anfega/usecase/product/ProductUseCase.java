package co.com.anfega.usecase.product;


import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import co.com.anfega.model.product.Product;
import co.com.anfega.model.product.gateways.ProductInputPort;
import co.com.anfega.model.product.gateways.ProductRepository;
import reactor.core.publisher.Mono;

public class ProductUseCase implements ProductInputPort {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    private static final String STOCK_NOT_NULL = "El stock del producto no puede ser nulo o negativo";

    public ProductUseCase(ProductRepository productRepository, BranchRepository branchRepository, FranchiseRepository franchiseRepository) {
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
        this.franchiseRepository = franchiseRepository;
    }

    @Override
    public Mono<Product> save(Product product, String branchName, String franchiseName) {
        if (product == null || product.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre del producto no puede estar vacío"));
        }
        if (product.getStock() == null || product.getStock() < 0) {
            return Mono.error(new IllegalArgumentException(STOCK_NOT_NULL));
        }
        if (branchName == null || branchName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre de la sucursal no puede estar vacío"));
        }
        if (franchiseName == null || franchiseName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre de la franquicia no puede estar vacío"));
        }
        return franchiseRepository.findByName(franchiseName)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La franquicia con nombre " + franchiseName + " no existe.")))
                .flatMap(franchise ->
                        branchRepository.findByNameAndFranchiseId(branchName, franchise.getId())
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("La sucursal con nombre " + branchName + " no existe.")))
                                .flatMap(branch -> productRepository.save(product, branch.getId()))
                );
    }

    @Override
    public Mono<Void> delete(String productId) {
        if (productId == null || productId.isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del producto no puede estar vacío"));
        }
        return productRepository.delete(productId);
    }

    @Override
    public Mono<Product> updateStock(String productId, long newStock) {
        if (newStock < 0 || productId == null || productId.isEmpty()) {
            return Mono.error(new IllegalArgumentException(STOCK_NOT_NULL));
        }
        return productRepository.updateStock(productId, newStock);
    }

}

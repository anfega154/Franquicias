package co.com.anfega.usecase.product;


import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.product.Product;
import co.com.anfega.model.product.gateways.ProductInputPort;
import co.com.anfega.model.product.gateways.ProductRepository;
import reactor.core.publisher.Mono;

public class ProductUseCase implements ProductInputPort {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public ProductUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public Mono<Product> save(Product product, String branchName) {
        if (product == null || product.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre del producto no puede estar vacío"));
        }
        if (product.getStock() == null || product.getStock() < 0) {
            return Mono.error(new IllegalArgumentException("El stock del producto no puede ser nulo o negativo"));
        }
        if (branchName == null || branchName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre de la suscursal no puede estar vacío"));
        }
        return branchRepository.findByName(branchName)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("La suscursal con nombre " + branchName + " no existe.")))
                .flatMap(branch -> productRepository.save(product, branchName));
    }

    @Override
    public Mono<Void> delete(String productId) {
        if (productId == null || productId.isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del producto no puede estar vacío"));
        }
        return productRepository.delete(productId);
    }
}

package co.com.anfega.model.product.gateways;

import co.com.anfega.model.product.Product;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product, String branchId);
    Mono<Void> delete(String productId);
    Mono<Product> updateStock(String productId, long newStock);
    Mono<Product> findTopByBranchId(String branchId);
    Mono<Product> update(Product product);
}

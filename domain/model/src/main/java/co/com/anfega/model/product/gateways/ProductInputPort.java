package co.com.anfega.model.product.gateways;

import co.com.anfega.model.product.Product;
import reactor.core.publisher.Mono;

public interface ProductInputPort {
    Mono<Product> save(Product product, String branchName, String franchiseName);
    Mono<Void> delete(String productId);
    Mono<Product> updateStock(String productId, long newStock);
}

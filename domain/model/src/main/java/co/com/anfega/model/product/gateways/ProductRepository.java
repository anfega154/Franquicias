package co.com.anfega.model.product.gateways;

import co.com.anfega.model.product.Product;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product, String branchId);
    Mono<Void> delete(String productId);
}

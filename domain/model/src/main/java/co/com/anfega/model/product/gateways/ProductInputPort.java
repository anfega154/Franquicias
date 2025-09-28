package co.com.anfega.model.product.gateways;

import co.com.anfega.model.product.Product;
import co.com.anfega.model.topproductperbranch.TopProductPerBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInputPort {
    Mono<Product> save(Product product, String branchName, String franchiseName);
    Mono<Void> delete(String productId);
    Mono<Product> updateStock(String productId, long newStock);
    Flux<TopProductPerBranch> getTopProductPerBranch(String franchiseName);
    Mono<Product> update(Product product);
}

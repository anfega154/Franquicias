package co.com.anfega.mongo;

import co.com.anfega.model.product.Product;
import co.com.anfega.model.product.gateways.ProductRepository;
import co.com.anfega.mongo.entity.ProductEntity;
import co.com.anfega.mongo.helper.AdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ProductRepositoryAdapter extends AdapterOperations<Product, ProductEntity, String, ProductDBRepository>
        implements ProductRepository {

    public ProductRepositoryAdapter(ProductDBRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Product.class));
    }

    @Override
    public Mono<Product> save(Product product, String branchId) {
        ProductEntity data = new ProductEntity();
        data.setName(product.getName());
        data.setStock(product.getStock());
        data.setBranchId(branchId);
        return repository.save(data)
                .map(savedData -> new Product(
                        savedData.getId(),
                        savedData.getName(),
                        savedData.getStock()
                ))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.error(new RuntimeException("Error al guardar el producto", e));
                });
    }
}

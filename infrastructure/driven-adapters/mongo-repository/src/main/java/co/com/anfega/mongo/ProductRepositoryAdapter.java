package co.com.anfega.mongo;

import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.model.product.Product;
import co.com.anfega.model.product.gateways.ProductRepository;
import co.com.anfega.mongo.entity.ProductEntity;
import co.com.anfega.mongo.helper.AdapterOperations;
import co.com.anfega.mongo.helper.MongoResilienceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ProductRepositoryAdapter extends AdapterOperations<Product, ProductEntity, String, ProductDBRepository>
        implements ProductRepository {

    private final MongoResilienceExecutor mongoResilienceExecutor;

    public ProductRepositoryAdapter(ProductDBRepository repository, ObjectMapper mapper, MongoResilienceExecutor mongoResilienceExecutor) {
        super(repository, mapper, d -> mapper.map(d, Product.class));
        this.mongoResilienceExecutor = mongoResilienceExecutor;
    }

    @Override
    public Mono<Product> save(Product product, String branchId) {
        ProductEntity data = new ProductEntity();
        data.setName(product.getName());
        data.setStock(product.getStock());
        data.setBranchId(branchId);
        log.info(Constants.LOG_PERSISTENCE_SAVE_PRODUCT_START, product.getName(), branchId);
        return mongoResilienceExecutor.executeMono("saving product", () -> repository.save(data))
                .map(savedData -> new Product(
                        savedData.getId(),
                        savedData.getName(),
                        savedData.getStock()
                ));
    }

    @Override
    public Mono<Void> delete(String productId) {
        log.info(Constants.LOG_PERSISTENCE_DELETE_PRODUCT_START, productId);
        return mongoResilienceExecutor.executeMono("finding product by id for delete", () -> repository.findById(productId))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, null)))
                .flatMap(product -> mongoResilienceExecutor.executeMono("deleting product by id", () -> repository.deleteById(productId).thenReturn(Boolean.TRUE)))
                .then();
    }

    @Override
    public Mono<Product> updateStock(String productId, long newStock) {
        log.info(Constants.LOG_PERSISTENCE_UPDATE_PRODUCT_STOCK_START, productId, newStock);
        return mongoResilienceExecutor.executeMono("finding product by id for stock update", () -> repository.findById(productId))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, null)))
                .flatMap(existingProduct -> {
                    existingProduct.setStock(newStock);
                    return mongoResilienceExecutor.executeMono("updating product stock", () -> repository.save(existingProduct));
                })
                .map(updated -> new Product(
                        updated.getId(),
                        updated.getName(),
                        updated.getStock()
                ));
    }

    @Override
    public Mono<Product> findTopByBranchId(String branchId) {
        log.info(Constants.LOG_PERSISTENCE_FIND_TOP_PRODUCT_START, branchId);
        return mongoResilienceExecutor.executeMono("finding top product by branch id", () -> repository.findFirstByBranchIdOrderByStockDesc(branchId))
                .map(pe -> new Product(pe.getId(), pe.getName(), pe.getStock()))
                ;
    }

    @Override
    public Mono<Product> update(Product product) {
        log.info(Constants.LOG_PERSISTENCE_UPDATE_PRODUCT_START, product.getId());
        return mongoResilienceExecutor.executeMono("finding product by id for update", () -> repository.findById(product.getId()))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, null)))
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    return mongoResilienceExecutor.executeMono("updating product", () -> repository.save(existingProduct));
                })
                .map(updated -> new Product(
                        updated.getId(),
                        updated.getName(),
                        updated.getStock()
                ));
    }

}

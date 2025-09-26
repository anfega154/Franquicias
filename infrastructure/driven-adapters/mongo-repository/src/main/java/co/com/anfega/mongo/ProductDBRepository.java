package co.com.anfega.mongo;

import co.com.anfega.mongo.entity.ProductEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Mono;

public interface ProductDBRepository extends ReactiveMongoRepository<ProductEntity, String>, ReactiveQueryByExampleExecutor<ProductEntity> {
    Mono<ProductEntity> findFirstByBranchIdOrderByStockDesc(String branchId);
}

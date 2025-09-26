package co.com.anfega.mongo;

import co.com.anfega.mongo.entity.FranchiseEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Mono;

public interface MongoDBRepository extends ReactiveMongoRepository<FranchiseEntity, String>, ReactiveQueryByExampleExecutor<FranchiseEntity> {
    Mono<FranchiseEntity> findByName(String name);
}

package co.com.anfega.mongo;

import co.com.anfega.mongo.entity.FranchiseEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

public interface MongoDBRepository extends ReactiveMongoRepository<FranchiseEntity, String>, ReactiveQueryByExampleExecutor<FranchiseEntity> {
}

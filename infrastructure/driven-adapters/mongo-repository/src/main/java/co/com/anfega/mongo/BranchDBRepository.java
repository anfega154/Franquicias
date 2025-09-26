package co.com.anfega.mongo;

import co.com.anfega.mongo.entity.BranchEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

public interface BranchDBRepository extends ReactiveMongoRepository<BranchEntity, String>, ReactiveQueryByExampleExecutor<BranchEntity> {
}

package co.com.anfega.mongo;

import co.com.anfega.mongo.entity.BranchEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchDBRepository extends ReactiveMongoRepository<BranchEntity, String>, ReactiveQueryByExampleExecutor<BranchEntity> {
    Mono<BranchEntity> findByNameAndFranchiseId(String name, String idFranchise);
    Flux<BranchEntity> findAllByFranchiseId(String franchiseId);
}

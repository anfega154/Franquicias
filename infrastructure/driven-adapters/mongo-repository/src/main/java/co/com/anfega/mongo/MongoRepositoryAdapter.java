package co.com.anfega.mongo;

import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import co.com.anfega.mongo.entity.FranchiseEntity;
import co.com.anfega.mongo.helper.AdapterOperations;
import co.com.anfega.mongo.helper.MongoResilienceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class MongoRepositoryAdapter extends AdapterOperations<Franchise, FranchiseEntity, String, MongoDBRepository>
        implements FranchiseRepository {

    private final MongoResilienceExecutor mongoResilienceExecutor;

    public MongoRepositoryAdapter(MongoDBRepository repository, ObjectMapper mapper, MongoResilienceExecutor mongoResilienceExecutor) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Franchise.class/* change for domain model */));
        this.mongoResilienceExecutor = mongoResilienceExecutor;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity data = new FranchiseEntity();
        data.setName(franchise.getName());

        log.info(Constants.LOG_PERSISTENCE_SAVE_FRANCHISE_START, franchise.getName());
        return mongoResilienceExecutor.executeMono("saving franchise", () -> repository.save(data))
                .map(this::toFranchise)
                .doOnSuccess(savedFranchise -> log.info(Constants.LOG_PERSISTENCE_SAVE_FRANCHISE_SUCCESS, savedFranchise.getId()));
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        log.info(Constants.LOG_PERSISTENCE_FIND_FRANCHISE_BY_NAME_START, name);
        return mongoResilienceExecutor.executeMono("finding franchise by name", () -> repository.findByName(name))
                .map(this::toFranchise);
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        log.info(Constants.LOG_PERSISTENCE_UPDATE_FRANCHISE_START, franchise.getId());
        return mongoResilienceExecutor.executeMono("finding franchise by id", () -> repository.findById(franchise.getId()))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.FRANCHISE_NOT_FOUND, franchise.getName(), null)))
                .flatMap(existingData -> {
                    existingData.setName(franchise.getName());
                    return mongoResilienceExecutor.executeMono("updating franchise", () -> repository.save(existingData));
                })
                .map(this::toFranchise);
    }

    private Franchise toFranchise(FranchiseEntity franchiseEntity) {
        Franchise franchise = new Franchise();
        franchise.setId(franchiseEntity.getId());
        franchise.setName(franchiseEntity.getName());
        return franchise;
    }
}

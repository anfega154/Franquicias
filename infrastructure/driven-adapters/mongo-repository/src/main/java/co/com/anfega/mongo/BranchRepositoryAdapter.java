package co.com.anfega.mongo;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.mongo.entity.BranchEntity;
import co.com.anfega.mongo.helper.AdapterOperations;
import co.com.anfega.mongo.helper.MongoResilienceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Repository
@Slf4j
public class BranchRepositoryAdapter extends AdapterOperations<Branch, BranchEntity, String, BranchDBRepository>
        implements BranchRepository {

    private final MongoResilienceExecutor mongoResilienceExecutor;

    public BranchRepositoryAdapter(BranchDBRepository repository, ObjectMapper mapper, MongoResilienceExecutor mongoResilienceExecutor) {
        super(repository, mapper, d -> mapper.map(d, Branch.class));
        this.mongoResilienceExecutor = mongoResilienceExecutor;
    }

    @Override
    public Mono<Branch> save(Branch branch, String idFranchise) {
        BranchEntity branchEntity = new BranchEntity();
        branchEntity.setName(branch.getName());
        branchEntity.setFranchiseId(idFranchise);
        log.info(Constants.LOG_PERSISTENCE_SAVE_BRANCH_START, branch.getName(), idFranchise);
        return mongoResilienceExecutor.executeMono("saving branch", () -> repository.save(branchEntity))
                .map(savedEntity -> new Branch(
                        savedEntity.getId(),
                        savedEntity.getName()
                ));
    }

    @Override
    public Mono<Branch> findByNameAndFranchiseId(String name, String franchiseId) {
        log.info(Constants.LOG_PERSISTENCE_FIND_BRANCH_BY_NAME_START, name, franchiseId);
        return mongoResilienceExecutor.executeMono(
                        "finding branch by name and franchise id",
                        () -> repository.findByNameAndFranchiseId(name, franchiseId)
                )
                .map(entity -> new Branch(
                        entity.getId(),
                        entity.getName()
                ));
    }

    @Override
    public Flux<Branch> findAllByFranchiseId(String franchiseId) {
        log.info(Constants.LOG_PERSISTENCE_FIND_BRANCHES_BY_FRANCHISE_ID_START, franchiseId);
        return mongoResilienceExecutor.executeFlux("finding branches by franchise id", () -> repository.findAllByFranchiseId(franchiseId))
                .map(be -> new Branch(be.getId(), be.getName(), Collections.emptyList()));
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        log.info(Constants.LOG_PERSISTENCE_UPDATE_BRANCH_START, branch.getId());
        return mongoResilienceExecutor.executeMono("finding branch by id", () -> repository.findById(branch.getId()))
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.BRANCH_NOT_FOUND, branch.getName(), null)))
                .flatMap(existingEntity -> {
                    existingEntity.setName(branch.getName());
                    return mongoResilienceExecutor.executeMono("updating branch", () -> repository.save(existingEntity));
                })
                .map(updatedEntity -> new Branch(
                        updatedEntity.getId(),
                        updatedEntity.getName()
                ));
    }
}

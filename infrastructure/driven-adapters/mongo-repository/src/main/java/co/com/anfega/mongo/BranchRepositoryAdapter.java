package co.com.anfega.mongo;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.mongo.entity.BranchEntity;
import co.com.anfega.mongo.helper.AdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class BranchRepositoryAdapter extends AdapterOperations<Branch, BranchEntity, String, BranchDBRepository>
        implements BranchRepository {

    public BranchRepositoryAdapter(BranchDBRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Branch.class));
    }

    @Override
    public Mono<Branch> save(Branch branch, String idFranchise) {
        BranchEntity branchEntity = new BranchEntity();
        branchEntity.setName(branch.getName());
        branchEntity.setFranchiseId(idFranchise);
        return repository.save(branchEntity)
                .map(savedEntity -> new Branch(
                        savedEntity.getId(),
                        savedEntity.getName()
                ))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.error(new RuntimeException("Error al guardar la sucursal", e));
                });
    }
}
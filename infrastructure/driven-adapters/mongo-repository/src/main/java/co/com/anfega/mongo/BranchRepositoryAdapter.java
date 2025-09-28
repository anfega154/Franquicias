package co.com.anfega.mongo;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.mongo.entity.BranchEntity;
import co.com.anfega.mongo.helper.AdapterOperations;
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

    @Override
    public Mono<Branch> findByNameAndFranchiseId(String name, String franchiseId) {
        return repository.findByNameAndFranchiseId(name, franchiseId)
                .map(entity -> new Branch(
                        entity.getId(),
                        entity.getName()
                ))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.error(new RuntimeException("Error al buscar la sucursal por nombre", e));
                });
    }

    @Override
    public Flux<Branch> findAllByFranchiseId(String franchiseId) {
        return repository.findAllByFranchiseId(franchiseId)
                .map(be -> new Branch(be.getId(), be.getName(), Collections.emptyList()));
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        return repository.findById(branch.getId())
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró la sucursal " + branch.getName())))
                .flatMap(existingEntity -> {
                    existingEntity.setName(branch.getName());
                    return repository.save(existingEntity);
                })
                .map(updatedEntity -> new Branch(
                        updatedEntity.getId(),
                        updatedEntity.getName()
                ))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return Mono.error(new RuntimeException("Error al actualizar la sucursal: " + e.getMessage(), e));
                });
    }
}
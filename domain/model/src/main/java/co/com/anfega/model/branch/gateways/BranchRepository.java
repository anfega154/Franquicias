package co.com.anfega.model.branch.gateways;

import co.com.anfega.model.branch.Branch;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch, String idFranchise);
    Mono<Branch> findByNameAndFranchiseId(String name, String idFranchise);
}

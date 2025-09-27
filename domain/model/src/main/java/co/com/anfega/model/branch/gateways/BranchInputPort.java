package co.com.anfega.model.branch.gateways;

import co.com.anfega.model.branch.Branch;
import reactor.core.publisher.Mono;

public interface BranchInputPort {
    Mono<Branch> save(Branch branch, String franchiseName);

}

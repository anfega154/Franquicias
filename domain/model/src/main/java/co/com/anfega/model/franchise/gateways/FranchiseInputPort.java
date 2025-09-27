package co.com.anfega.model.franchise.gateways;

import co.com.anfega.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseInputPort {
    Mono<Franchise> save(Franchise franchise);
}

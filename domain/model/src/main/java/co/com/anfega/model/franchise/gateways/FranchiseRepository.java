package co.com.anfega.model.franchise.gateways;

import co.com.anfega.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise>save(Franchise franchise);
    Mono<Franchise> findByName(String name);
    Mono<Franchise> update(Franchise franchise);
}

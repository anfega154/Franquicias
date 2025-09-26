package co.com.anfega.usecase.franchise;

import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseInputPort;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import reactor.core.publisher.Mono;

public class FranchiseUseCase implements FranchiseInputPort {
    private final FranchiseRepository franchiseRepository;

    public FranchiseUseCase(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        if(franchise.getName()==null || franchise.getName().isEmpty()){
            return Mono.error(new IllegalArgumentException("El nombre de la franquicia no puede ser nulo o vacío"));
        }
        return franchiseRepository.save(franchise);
    }
}

package co.com.anfega.usecase.franchise;

import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseInputPort;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import reactor.core.publisher.Mono;

public class FranchiseUseCase implements FranchiseInputPort {
    private final FranchiseRepository franchiseRepository;

    private static final String NAME_EMPTY_ERROR = "El nombre de la franquicia es obligatorio.";

    public FranchiseUseCase(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        if (franchise.getId() != null && !franchise.getId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El id de la franquicia debe ser nulo o vacío al crear una nueva franquicia"));
        }
        if (franchise.getName() == null || franchise.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException(NAME_EMPTY_ERROR));
        }
        return franchiseRepository.save(franchise);
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        if (franchise.getId() == null || franchise.getId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El id de la franquicia no puede ser nulo o vacío"));
        }
        if (franchise.getName() == null || franchise.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException(NAME_EMPTY_ERROR));
        }
        return franchiseRepository.update(franchise);
    }
}

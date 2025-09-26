package co.com.anfega.usecase.branch;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.branch.gateways.BranchInputPort;
import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import reactor.core.publisher.Mono;

public class BranchUseCase implements BranchInputPort {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public BranchUseCase(FranchiseRepository franchiseRepository, BranchRepository branchRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public Mono<Branch> save(Branch branch, String franchiseName) {
        if(branch == null || branch.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre de la sucursal no puede estar vacío"));
        }
        if(franchiseName == null || franchiseName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre de la franquicia no puede estar vacío"));
        }
        return franchiseRepository.findByName(franchiseName)
                .switchIfEmpty(Mono.error(new java.util.NoSuchElementException("La franquicia no existe")))
                .flatMap(franchise -> branchRepository.save(branch, franchise.getId()));
    }
}

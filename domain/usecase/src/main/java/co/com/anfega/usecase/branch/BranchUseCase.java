package co.com.anfega.usecase.branch;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.branch.gateways.BranchInputPort;
import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

public class BranchUseCase implements BranchInputPort {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    private static final String NAME_REQUIRED = "El nombre de la sucursal es obligatorio";

    public BranchUseCase(FranchiseRepository franchiseRepository, BranchRepository branchRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public Mono<Branch> save(Branch branch, String franchiseName) {
        if (branch.getId() != null && !branch.getId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("La sucursal no debe tener un id asignado"));
        }
        if (branch.getName() == null || branch.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException(NAME_REQUIRED));
        }
        if (franchiseName == null || franchiseName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre de la franquicia es obligatorio"));
        }
        return franchiseRepository.findByName(franchiseName)
                .switchIfEmpty(Mono.error(new NoSuchElementException("La franquicia no existe")))
                .flatMap(franchise -> branchRepository.save(branch, franchise.getId()));
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        if (branch.getName() == null || branch.getName().isEmpty()) {
            return Mono.error(new IllegalArgumentException(NAME_REQUIRED));
        }
        if (branch.getId() == null || branch.getId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El id de la sucursal es obligatorio"));
        }
        return branchRepository.update(branch);
    }
}

package co.com.anfega.usecase.branch;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BranchUseCaseTest {

    private FranchiseRepository franchiseRepository;
    private BranchRepository branchRepository;
    private BranchUseCase branchUseCase;

    @BeforeEach
    void setUp() {
        franchiseRepository = Mockito.mock(FranchiseRepository.class);
        branchRepository = Mockito.mock(BranchRepository.class);
        branchUseCase = new BranchUseCase(franchiseRepository, branchRepository);
    }

    // ---------------- SAVE ----------------

    @Test
    void shouldReturnErrorWhenBranchIdIsNotNullOnSave() {
        Branch branch = new Branch();
        branch.setId("123");
        branch.setName("Sucursal 1");

        StepVerifier.create(branchUseCase.save(branch, "franchise"))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException &&
                        t.getMessage().equals("La sucursal no debe tener un id asignado"))
                .verify();

        verifyNoInteractions(franchiseRepository);
        verifyNoInteractions(branchRepository);
    }

    @Test
    void shouldReturnErrorWhenBranchNameIsNullOnSave() {
        Branch branch = new Branch();
        branch.setName(null);

        StepVerifier.create(branchUseCase.save(branch, "franchise"))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException &&
                        t.getMessage().equals("El nombre de la sucursal es obligatorio"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenBranchNameIsEmptyOnSave() {
        Branch branch = new Branch();
        branch.setName("");

        StepVerifier.create(branchUseCase.save(branch, "franchise"))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException &&
                        t.getMessage().equals("El nombre de la sucursal es obligatorio"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsNullOnSave() {
        Branch branch = new Branch();
        branch.setName("Sucursal 1");

        StepVerifier.create(branchUseCase.save(branch, null))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException &&
                        t.getMessage().equals("El nombre de la franquicia es obligatorio"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsEmptyOnSave() {
        Branch branch = new Branch();
        branch.setName("Sucursal 1");

        StepVerifier.create(branchUseCase.save(branch, ""))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException &&
                        t.getMessage().equals("El nombre de la franquicia es obligatorio"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenFranchiseDoesNotExistOnSave() {
        Branch branch = new Branch();
        branch.setName("Sucursal 1");

        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.save(branch, "franchise"))
                .expectErrorMatches(t -> t instanceof NoSuchElementException &&
                        t.getMessage().equals("La franquicia no existe"))
                .verify();

        verify(franchiseRepository, times(1)).findByName("franchise");
        verifyNoInteractions(branchRepository);
    }

    @Test
    void shouldSaveBranchWhenFranchiseExists() {
        Branch branch = new Branch();
        branch.setName("Sucursal 1");

        Franchise franchise = new Franchise();
        franchise.setId("123");
        franchise.setName("franchise");

        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.just(franchise));
        when(branchRepository.save(any(Branch.class), eq("123"))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchUseCase.save(branch, "franchise"))
                .expectNext(branch)
                .verifyComplete();

        verify(franchiseRepository, times(1)).findByName("franchise");
        verify(branchRepository, times(1)).save(branch, "123");
    }

    // ---------------- UPDATE ----------------

    @Test
    void shouldReturnErrorWhenBranchNameIsNullOnUpdate() {
        Branch branch = new Branch();
        branch.setId("123");
        branch.setName(null);

        StepVerifier.create(branchUseCase.update(branch))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException &&
                        t.getMessage().equals("El nombre de la sucursal es obligatorio"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenBranchNameIsEmptyOnUpdate() {
        Branch branch = new Branch();
        branch.setId("123");
        branch.setName("");

        StepVerifier.create(branchUseCase.update(branch))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException &&
                        t.getMessage().equals("El nombre de la sucursal es obligatorio"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenBranchIdIsNullOnUpdate() {
        Branch branch = new Branch();
        branch.setId(null);
        branch.setName("Sucursal 1");

        StepVerifier.create(branchUseCase.update(branch))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException &&
                        t.getMessage().equals("El id de la sucursal es obligatorio"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenBranchIdIsEmptyOnUpdate() {
        Branch branch = new Branch();
        branch.setId("");
        branch.setName("Sucursal 1");

        StepVerifier.create(branchUseCase.update(branch))
                .expectErrorMatches(t -> t instanceof IllegalArgumentException &&
                        t.getMessage().equals("El id de la sucursal es obligatorio"))
                .verify();
    }

    @Test
    void shouldUpdateBranchWhenValid() {
        Branch branch = new Branch();
        branch.setId("123");
        branch.setName("Sucursal Actualizada");

        when(branchRepository.update(any(Branch.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchUseCase.update(branch))
                .expectNext(branch)
                .verifyComplete();

        verify(branchRepository, times(1)).update(branch);
    }
}

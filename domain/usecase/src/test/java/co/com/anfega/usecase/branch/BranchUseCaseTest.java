package co.com.anfega.usecase.branch;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        franchiseRepository = mock(FranchiseRepository.class);
        branchRepository = mock(BranchRepository.class);
        branchUseCase = new BranchUseCase(franchiseRepository, branchRepository);
    }

    // ---------------- SAVE ----------------

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

        verify(franchiseRepository).findByName("franchise");
        verify(branchRepository).save(branch, "123");
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

        verify(franchiseRepository).findByName("franchise");
        verifyNoInteractions(branchRepository);
    }

    // ---------------- UPDATE ----------------

    @Test
    void shouldUpdateBranchSuccessfully() {
        Branch branch = new Branch();
        branch.setId("123");
        branch.setName("Sucursal Actualizada");

        when(branchRepository.update(any(Branch.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(branchUseCase.update(branch))
                .expectNext(branch)
                .verifyComplete();

        verify(branchRepository).update(branch);
    }

    @Test
    void shouldPropagateErrorWhenUpdateFails() {
        Branch branch = new Branch();
        branch.setId("123");
        branch.setName("Sucursal Fallida");

        when(branchRepository.update(any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(branchUseCase.update(branch))
                .expectErrorMatches(t -> t instanceof RuntimeException &&
                        t.getMessage().equals("DB error"))
                .verify();

        verify(branchRepository).update(branch);
    }
}

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

    @Test
    void shouldReturnErrorWhenBranchIsNull() {
        StepVerifier.create(branchUseCase.save(null, "franchise"))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre de la sucursal no puede estar vacío"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenBranchNameIsEmpty() {
        Branch branch = new Branch();
        branch.setName("");

        StepVerifier.create(branchUseCase.save(branch, "franchise"))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre de la sucursal no puede estar vacío"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsNull() {
        Branch branch = new Branch();
        branch.setName("Sucursal 1");

        StepVerifier.create(branchUseCase.save(branch, null))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre de la franquicia no puede estar vacío"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsEmpty() {
        Branch branch = new Branch();
        branch.setName("Sucursal 1");

        StepVerifier.create(branchUseCase.save(branch, ""))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre de la franquicia no puede estar vacío"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenFranchiseDoesNotExist() {
        Branch branch = new Branch();
        branch.setName("Sucursal 1");

        when(franchiseRepository.findByName("franchise")).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.save(branch, "franchise"))
                .expectErrorMatches(throwable -> throwable instanceof NoSuchElementException &&
                        throwable.getMessage().equals("La franquicia no existe"))
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
}

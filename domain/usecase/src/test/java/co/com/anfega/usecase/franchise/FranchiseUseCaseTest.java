package co.com.anfega.usecase.franchise;

import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FranchiseUseCaseTest {

    private FranchiseRepository franchiseRepository;
    private FranchiseUseCase franchiseUseCase;

    @BeforeEach
    void setUp() {
        franchiseRepository = mock(FranchiseRepository.class);
        franchiseUseCase = new FranchiseUseCase(franchiseRepository);
    }

    // ----------------- SAVE -----------------

    @Test
    void shouldSaveFranchiseSuccessfully() {
        Franchise franchise = new Franchise();
        franchise.setName("Franquicia Test");

        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.save(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).save(franchise);
    }

    // ----------------- UPDATE -----------------

    @Test
    void shouldUpdateFranchiseSuccessfully() {
        Franchise franchise = new Franchise();
        franchise.setId("123");
        franchise.setName("Franquicia Actualizada");

        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.update(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).update(franchise);
    }

    // ----------------- ERROR -----------------

    @Test
    void shouldReturnErrorWhenSaveFails() {
        Franchise franchise = new Franchise();
        franchise.setName("Franquicia Error");

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(franchiseUseCase.save(franchise))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("DB error"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenUpdateFails() {
        Franchise franchise = new Franchise();
        franchise.setId("123");
        franchise.setName("Franquicia Error");

        when(franchiseRepository.update(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(franchiseUseCase.update(franchise))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("DB error"))
                .verify();
    }

}

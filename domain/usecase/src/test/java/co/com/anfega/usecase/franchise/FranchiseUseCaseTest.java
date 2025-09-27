package co.com.anfega.usecase.franchise;

import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FranchiseUseCaseTest {

    private FranchiseRepository franchiseRepository;
    private FranchiseUseCase franchiseUseCase;

    @BeforeEach
    void setUp() {
        franchiseRepository = Mockito.mock(FranchiseRepository.class);
        franchiseUseCase = new FranchiseUseCase(franchiseRepository);
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsNull() {
        Franchise franchise = new Franchise();
        franchise.setName(null);

        StepVerifier.create(franchiseUseCase.save(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre de la franquicia no puede ser nulo o vacío"))
                .verify();

        verifyNoInteractions(franchiseRepository);
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsEmpty() {
        Franchise franchise = new Franchise();
        franchise.setName("");

        StepVerifier.create(franchiseUseCase.save(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre de la franquicia no puede ser nulo o vacío"))
                .verify();

        verifyNoInteractions(franchiseRepository);
    }

    @Test
    void shouldSaveFranchiseWhenNameIsValid() {
        Franchise franchise = new Franchise();
        franchise.setName("Franquicia Test");

        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.save(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).save(franchise);
    }
}

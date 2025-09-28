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

    // ----------------- SAVE -----------------

    @Test
    void shouldReturnErrorWhenFranchiseIdIsNotNullOnSave() {
        Franchise franchise = new Franchise();
        franchise.setId("123");
        franchise.setName("Franquicia Test");

        StepVerifier.create(franchiseUseCase.save(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El id de la franquicia debe ser nulo o vacío al crear una nueva franquicia"))
                .verify();

        verifyNoInteractions(franchiseRepository);
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsNullOnSave() {
        Franchise franchise = new Franchise();
        franchise.setName(null);

        StepVerifier.create(franchiseUseCase.save(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre de la franquicia no puede ser nulo o vacío"))
                .verify();

        verifyNoInteractions(franchiseRepository);
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsEmptyOnSave() {
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

    // ----------------- UPDATE -----------------

    @Test
    void shouldReturnErrorWhenFranchiseIdIsNullOnUpdate() {
        Franchise franchise = new Franchise();
        franchise.setId(null);
        franchise.setName("Franquicia Test");

        StepVerifier.create(franchiseUseCase.update(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El id de la franquicia no puede ser nulo o vacío"))
                .verify();

        verifyNoInteractions(franchiseRepository);
    }

    @Test
    void shouldReturnErrorWhenFranchiseIdIsEmptyOnUpdate() {
        Franchise franchise = new Franchise();
        franchise.setId("");
        franchise.setName("Franquicia Test");

        StepVerifier.create(franchiseUseCase.update(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El id de la franquicia no puede ser nulo o vacío"))
                .verify();

        verifyNoInteractions(franchiseRepository);
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsNullOnUpdate() {
        Franchise franchise = new Franchise();
        franchise.setId("123");
        franchise.setName(null);

        StepVerifier.create(franchiseUseCase.update(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre de la franquicia no puede ser nulo o vacío"))
                .verify();

        verifyNoInteractions(franchiseRepository);
    }

    @Test
    void shouldReturnErrorWhenFranchiseNameIsEmptyOnUpdate() {
        Franchise franchise = new Franchise();
        franchise.setId("123");
        franchise.setName("");

        StepVerifier.create(franchiseUseCase.update(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El nombre de la franquicia no puede ser nulo o vacío"))
                .verify();

        verifyNoInteractions(franchiseRepository);
    }

    @Test
    void shouldUpdateFranchiseWhenIdAndNameAreValid() {
        Franchise franchise = new Franchise();
        franchise.setId("123");
        franchise.setName("Franquicia Actualizada");

        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.update(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).update(franchise);
    }
}

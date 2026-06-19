package co.com.anfega.usecase.franchise;

import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseInputPort;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import reactor.core.publisher.Mono;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FranchiseUseCase implements FranchiseInputPort {
    private static final Logger LOGGER = Logger.getLogger(FranchiseUseCase.class.getName());
    private final FranchiseRepository franchiseRepository;

    public FranchiseUseCase(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_SAVE_FRANCHISE_START, franchise.getName()));
        return franchiseRepository.save(franchise)
                .doOnSuccess(savedFranchise -> LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_SAVE_FRANCHISE_SUCCESS, savedFranchise.getId())));
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_UPDATE_FRANCHISE_START, franchise.getId()));
        return franchiseRepository.update(franchise)
                .doOnSuccess(savedFranchise -> LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_UPDATE_FRANCHISE_SUCCESS, savedFranchise.getId())));
    }
}

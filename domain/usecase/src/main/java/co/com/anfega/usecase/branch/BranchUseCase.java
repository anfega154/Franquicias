package co.com.anfega.usecase.branch;

import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.branch.gateways.BranchInputPort;
import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import reactor.core.publisher.Mono;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BranchUseCase implements BranchInputPort {
    private static final Logger LOGGER = Logger.getLogger(BranchUseCase.class.getName());

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public BranchUseCase(FranchiseRepository franchiseRepository, BranchRepository branchRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public Mono<Branch> save(Branch branch, String franchiseName) {
        LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_SAVE_BRANCH_START, branch.getName(), franchiseName));
        return franchiseRepository.findByName(franchiseName)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.FRANCHISE_NOT_FOUND, franchiseName, null)))
                .flatMap(franchise -> branchRepository.save(branch, franchise.getId()))
                .doOnSuccess(savedBranch -> LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_SAVE_BRANCH_SUCCESS, savedBranch.getId())));
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_UPDATE_BRANCH_START, branch.getId()));
        return branchRepository.update(branch)
                .doOnSuccess(updatedBranch -> LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_UPDATE_BRANCH_SUCCESS, updatedBranch.getId())));
    }
}

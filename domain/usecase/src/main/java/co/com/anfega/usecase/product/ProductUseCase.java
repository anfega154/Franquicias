package co.com.anfega.usecase.product;


import co.com.anfega.model.branch.gateways.BranchRepository;
import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.model.franchise.gateways.FranchiseRepository;
import co.com.anfega.model.product.Product;
import co.com.anfega.model.product.gateways.ProductInputPort;
import co.com.anfega.model.product.gateways.ProductRepository;
import co.com.anfega.model.topproductperbranch.TopProductPerBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductUseCase implements ProductInputPort {
    private static final Logger LOGGER = Logger.getLogger(ProductUseCase.class.getName());

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public ProductUseCase(ProductRepository productRepository, BranchRepository branchRepository, FranchiseRepository franchiseRepository) {
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
        this.franchiseRepository = franchiseRepository;
    }

    @Override
    public Mono<Product> save(Product product, String branchName, String franchiseName) {
        LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_SAVE_PRODUCT_START, product.getName(), branchName, franchiseName));
        return franchiseRepository.findByName(franchiseName)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.FRANCHISE_NOT_FOUND, franchiseName, null)))
                .flatMap(franchise ->
                        branchRepository.findByNameAndFranchiseId(branchName, franchise.getId())
                                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.BRANCH_NOT_FOUND, branchName, null)))
                                .flatMap(branch -> productRepository.save(product, branch.getId()))
                )
                .doOnSuccess(savedProduct -> LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_SAVE_PRODUCT_SUCCESS, savedProduct.getId())));
    }

    @Override
    public Mono<Void> delete(String productId) {
        LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_DELETE_PRODUCT_START, productId));
        return productRepository.delete(productId)
                .doOnSuccess(ignored -> LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_DELETE_PRODUCT_SUCCESS, productId)));
    }

    @Override
    public Mono<Product> updateStock(String productId, long newStock) {
        LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_UPDATE_STOCK_START, productId, newStock));
        return productRepository.updateStock(productId, newStock)
                .doOnSuccess(updatedProduct -> LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_UPDATE_STOCK_SUCCESS, updatedProduct.getId(), updatedProduct.getStock())));
    }

    @Override
    public Flux<TopProductPerBranch> getTopProductPerBranch(String franchiseName) {
        LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_GET_TOP_PRODUCTS_START, franchiseName));
        return franchiseRepository.findByName(franchiseName)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.FRANCHISE_NOT_FOUND, franchiseName, null)))
                .flatMapMany(franchise ->
                        branchRepository.findAllByFranchiseId(franchise.getId())
                                .flatMap(branch ->
                                        productRepository.findTopByBranchId(branch.getId())
                                                .map(product -> new TopProductPerBranch(
                                                        branch.getId(),
                                                        branch.getName(),
                                                        product.getId(),
                                                        product.getName(),
                                                        product.getStock()
                                                ))
                                )
                )
                .doOnNext(result -> LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_GET_TOP_PRODUCTS_SUCCESS, result.getBranchId(), result.getProductId())));
    }

    @Override
    public Mono<Product> update(Product product) {
        LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_UPDATE_PRODUCT_START, product.getId()));
        return productRepository.update(product)
                .doOnSuccess(updatedProduct -> LOGGER.log(Level.INFO, () -> String.format(Constants.LOG_USE_CASE_UPDATE_PRODUCT_SUCCESS, updatedProduct.getId())));
    }
}

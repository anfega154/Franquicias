package co.com.anfega.api;

import co.com.anfega.api.dto.*;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.mapper.BranchDTOMapper;
import co.com.anfega.api.mapper.FranchiseDTOMapper;
import co.com.anfega.api.mapper.ProductDTOMapper;
import co.com.anfega.api.util.EntryPointUtils;
import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.model.branch.gateways.BranchInputPort;
import co.com.anfega.model.franchise.gateways.FranchiseInputPort;
import co.com.anfega.model.product.gateways.ProductInputPort;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler extends BaseHandler {

    private final FranchiseDTOMapper franchiseDTOMapper;
    private final FranchiseInputPort franchiseInputPort;
    private final BranchDTOMapper branchDTOMapper;
    private final BranchInputPort branchInputPort;
    private final ProductInputPort productInputPort;
    private final ProductDTOMapper productDTOMapper;
    private final Validator validator;

    public Mono<ServerResponse> listenSaveFranchise(ServerRequest serverRequest) {
        String traceId = EntryPointUtils.getTraceId(serverRequest);
        log.info(Constants.LOG_CREATE_FRANCHISE_START, traceId);
        return bodyToMonoValidated(validator, serverRequest, CreateFranchiseDTO.class)
                .switchIfEmpty(Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() != null && !dto.getId().isEmpty()) {
                        return Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.FRANCHISE_ID_MUST_BE_EMPTY));
                    }
                    return Mono.just(dto);
                })
                .map(franchiseDTOMapper::toModel)
                .flatMap(franchiseInputPort::save)
                .map(franchiseDTOMapper::toResponse)
                .doOnSuccess(response -> log.info(Constants.LOG_CREATE_FRANCHISE_SUCCESS, response.id(), traceId))
                .doOnError(throwable -> log.warn(Constants.LOG_REQUEST_ERROR, traceId, throwable.getMessage()))
                .flatMap(response -> created(traceId, Constants.CREATE_FRANCHISE_SUCCESS_MESSAGE, response));
    }

    public Mono<ServerResponse> listenUpdateFranchise(ServerRequest serverRequest) {
        String traceId = EntryPointUtils.getTraceId(serverRequest);
        log.info(Constants.LOG_UPDATE_FRANCHISE_START, traceId);
        return bodyToMonoValidated(validator, serverRequest, CreateFranchiseDTO.class)
                .switchIfEmpty(Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() == null || dto.getId().isEmpty()) {
                        return Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.FRANCHISE_ID_REQUIRED));
                    }
                    return Mono.just(dto);
                })
                .map(franchiseDTOMapper::toModel)
                .flatMap(franchiseInputPort::update)
                .map(franchiseDTOMapper::toResponse)
                .doOnSuccess(response -> log.info(Constants.LOG_UPDATE_FRANCHISE_SUCCESS, response.id(), traceId))
                .doOnError(throwable -> log.warn(Constants.LOG_REQUEST_ERROR, traceId, throwable.getMessage()))
                .flatMap(response -> ok(traceId, Constants.UPDATE_FRANCHISE_SUCCESS_MESSAGE, response));
    }

    public Mono<ServerResponse> listenSaveBranch(ServerRequest serverRequest) {
        String traceId = EntryPointUtils.getTraceId(serverRequest);
        log.info(Constants.LOG_CREATE_BRANCH_START, traceId);
        return bodyToMonoValidated(validator, serverRequest, CreateBranchDTO.class)
                .switchIfEmpty(Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() != null && !dto.getId().isEmpty()) {
                        return Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.BRANCH_ID_MUST_BE_EMPTY));
                    }
                    var branch = branchDTOMapper.toModel(dto);
                    var franchiseName = dto.getFranchiseName();
                    return branchInputPort.save(branch, franchiseName)
                            .map(branchDTOMapper::toResponse);
                })
                .doOnSuccess(response -> log.info(Constants.LOG_CREATE_BRANCH_SUCCESS, response.id(), traceId))
                .doOnError(throwable -> log.warn(Constants.LOG_REQUEST_ERROR, traceId, throwable.getMessage()))
                .flatMap(response -> created(traceId, Constants.CREATE_BRANCH_SUCCESS_MESSAGE, response));
    }

    public Mono<ServerResponse> listenUpdateBranch(ServerRequest serverRequest) {
        String traceId = EntryPointUtils.getTraceId(serverRequest);
        log.info(Constants.LOG_UPDATE_BRANCH_START, traceId);
        return bodyToMonoValidated(validator, serverRequest, CreateBranchDTO.class)
                .switchIfEmpty(Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() == null || dto.getId().isEmpty()) {
                        return Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.BRANCH_ID_REQUIRED));
                    }
                    return Mono.just(dto);
                })
                .map(branchDTOMapper::toModel)
                .flatMap(branchInputPort::update)
                .map(branchDTOMapper::toResponse)
                .doOnSuccess(response -> log.info(Constants.LOG_UPDATE_BRANCH_SUCCESS, response.id(), traceId))
                .doOnError(throwable -> log.warn(Constants.LOG_REQUEST_ERROR, traceId, throwable.getMessage()))
                .flatMap(response -> ok(traceId, Constants.UPDATE_BRANCH_SUCCESS_MESSAGE, response));
    }

    public Mono<ServerResponse> listenSaveProduct(ServerRequest serverRequest) {
        String traceId = EntryPointUtils.getTraceId(serverRequest);
        log.info(Constants.LOG_CREATE_PRODUCT_START, traceId);
        return bodyToMonoValidated(validator, serverRequest, CreateProductDTO.class)
                .switchIfEmpty(Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() != null && !dto.getId().isEmpty()) {
                        return Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.PRODUCT_ID_MUST_BE_EMPTY));
                    }
                    var product = productDTOMapper.toModel(dto);
                    var branchName = dto.getBranchName();
                    var franchiseName = dto.getFranchiseName();
                    return productInputPort.save(product, branchName, franchiseName)
                            .map(productDTOMapper::toResponse);
                })
                .doOnSuccess(response -> log.info(Constants.LOG_CREATE_PRODUCT_SUCCESS, response.id(), traceId))
                .doOnError(throwable -> log.warn(Constants.LOG_REQUEST_ERROR, traceId, throwable.getMessage()))
                .flatMap(response -> created(traceId, Constants.CREATE_PRODUCT_SUCCESS_MESSAGE, response));
    }

    public Mono<ServerResponse> listenUpdateProduct(ServerRequest serverRequest) {
        String traceId = EntryPointUtils.getTraceId(serverRequest);
        log.info(Constants.LOG_UPDATE_PRODUCT_START, traceId);
        return bodyToMonoValidated(validator, serverRequest, CreateProductDTO.class)
                .switchIfEmpty(Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() == null || dto.getId().isEmpty()) {
                        return Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.PRODUCT_ID_REQUIRED));
                    }
                    return Mono.just(dto);
                })
                .map(productDTOMapper::toModel)
                .flatMap(productInputPort::update)
                .map(productDTOMapper::toResponse)
                .doOnSuccess(response -> log.info(Constants.LOG_UPDATE_PRODUCT_SUCCESS, response.id(), traceId))
                .doOnError(throwable -> log.warn(Constants.LOG_REQUEST_ERROR, traceId, throwable.getMessage()))
                .flatMap(response -> ok(traceId, Constants.UPDATE_PRODUCT_SUCCESS_MESSAGE, response));
    }

    public Mono<ServerResponse> listenDeleteProduct(ServerRequest serverRequest) {
        String traceId = EntryPointUtils.getTraceId(serverRequest);
        String productId = serverRequest.queryParam(Constants.QUERY_PARAM_ID)
                .orElseThrow(() -> BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.PRODUCT_QUERY_ID_REQUIRED));
        log.info(Constants.LOG_DELETE_PRODUCT_START, productId, traceId);
        return productInputPort.delete(productId)
                .doOnSuccess(ignored -> log.info(Constants.LOG_DELETE_PRODUCT_SUCCESS, productId, traceId))
                .doOnError(throwable -> log.warn(Constants.LOG_REQUEST_ERROR, traceId, throwable.getMessage()))
                .then(ok(traceId, Constants.DELETE_PRODUCT_SUCCESS_MESSAGE));

    }

    public Mono<ServerResponse> listenUpdateStockProduct(ServerRequest serverRequest) {
        String traceId = EntryPointUtils.getTraceId(serverRequest);
        String productId = serverRequest.queryParam(Constants.QUERY_PARAM_ID)
                .orElseThrow(() -> BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.PRODUCT_QUERY_ID_REQUIRED));
        log.info(Constants.LOG_UPDATE_STOCK_START, productId, traceId);
        return bodyToMonoValidated(validator, serverRequest, UpdateStockProductRequestDTO.class)
                .switchIfEmpty(Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getStock() == null || dto.getStock() < 0) {
                        return Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.PRODUCT_STOCK_REQUIRED));
                    }
                    return productInputPort.updateStock(productId, dto.getStock())
                            .map(productDTOMapper::toResponse);
                })
                .doOnSuccess(response -> log.info(Constants.LOG_UPDATE_STOCK_SUCCESS, productId, traceId))
                .doOnError(throwable -> log.warn(Constants.LOG_REQUEST_ERROR, traceId, throwable.getMessage()))
                .flatMap(response -> ok(traceId, Constants.UPDATE_PRODUCT_STOCK_SUCCESS_MESSAGE, response));
    }

    public Mono<ServerResponse> listenGetTopProductsPerBranch(ServerRequest serverRequest) {
        String traceId = EntryPointUtils.getTraceId(serverRequest);
        String franchiseName = serverRequest.pathVariable("franchiseName");
        if (franchiseName.isEmpty()) {
            return Mono.error(BusinessException.withMessage(ErrorCode.VALIDATION_ERROR, traceId, Constants.FRANCHISE_NAME_REQUIRED));
        }
        log.info(Constants.LOG_GET_TOP_PRODUCTS_START, franchiseName, traceId);
        return productInputPort.getTopProductPerBranch(franchiseName)
                .collectList()
                .doOnSuccess(result -> log.info(Constants.LOG_GET_TOP_PRODUCTS_SUCCESS, result.size(), traceId))
                .doOnError(throwable -> log.warn(Constants.LOG_REQUEST_ERROR, traceId, throwable.getMessage()))
                .flatMap(result -> ok(traceId, Constants.QUERY_SUCCESS_MESSAGE, result));
    }
}

package co.com.anfega.api;

import co.com.anfega.api.dto.*;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.mapper.BranchDTOMapper;
import co.com.anfega.api.mapper.FranchiseDTOMapper;
import co.com.anfega.api.mapper.ProductDTOMapper;
import co.com.anfega.model.branch.gateways.BranchInputPort;
import co.com.anfega.model.franchise.gateways.FranchiseInputPort;
import co.com.anfega.model.product.gateways.ProductInputPort;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler extends BaseHandler {

    private final FranchiseDTOMapper franchiseDTOMapper;
    private final FranchiseInputPort franchiseInputPort;
    private final BranchDTOMapper branchDTOMapper;
    private final BranchInputPort branchInputPort;
    private final ProductInputPort productInputPort;
    private final ProductDTOMapper productDTOMapper;
    private final Validator validator;

    private static final String BODY_EMPTY_ERROR = "El body no puede estar vacío";

    public Mono<ServerResponse> listenSaveFranchise(ServerRequest serverRequest) {
        return bodyToMonoValidated(validator, serverRequest, CreateFranchiseDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() != null && !dto.getId().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("El id de la franquicia debe ser nulo o vacío al crear una nueva franquicia"));
                    }
                    return Mono.just(dto);
                })
                .map(franchiseDTOMapper::toModel)
                .flatMap(franchiseInputPort::save)
                .map(franchiseDTOMapper::toResponse)
                .flatMap(response -> created("Franquicia creada con exito", response));
    }

    public Mono<ServerResponse> listenUpdateFranchise(ServerRequest serverRequest) {
        return bodyToMonoValidated(validator, serverRequest, CreateFranchiseDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() == null || dto.getId().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("El id de la franquicia es obligatorio para actualizar una franquicia"));
                    }
                    return Mono.just(dto);
                })
                .map(franchiseDTOMapper::toModel)
                .flatMap(franchiseInputPort::update)
                .map(franchiseDTOMapper::toResponse)
                .flatMap(response -> ok("Franquicia actualizada con exito", response));
    }

    public Mono<ServerResponse> listenSaveBranch(ServerRequest serverRequest) {
        return bodyToMonoValidated(validator, serverRequest, CreateBranchDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() != null && !dto.getId().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("La sucursal no debe tener un id asignado"));
                    }
                    var branch = branchDTOMapper.toModel(dto);
                    var franchiseName = dto.getFranchiseName();
                    return branchInputPort.save(branch, franchiseName)
                            .map(branchDTOMapper::toResponse);
                })
                .flatMap(response -> created("Sucursal creada con exito", response));
    }

    public Mono<ServerResponse> listenUpdateBranch(ServerRequest serverRequest) {
        return bodyToMonoValidated(validator, serverRequest, CreateBranchDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() == null || dto.getId().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("El id de la sucursal es obligatorio para actualizar una sucursal"));
                    }
                    return Mono.just(dto);
                })
                .map(branchDTOMapper::toModel)
                .flatMap(branchInputPort::update)
                .map(branchDTOMapper::toResponse)
                .flatMap(response -> ok("Sucursal actualizada con exito", response));
    }

    public Mono<ServerResponse> listenSaveProduct(ServerRequest serverRequest) {
        return bodyToMonoValidated(validator, serverRequest, CreateProductDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() != null && !dto.getId().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("El id del producto debe ser nulo o vacío al crear un nuevo producto"));
                    }
                    var product = productDTOMapper.toModel(dto);
                    var branchName = dto.getBranchName();
                    var franchiseName = dto.getFranchiseName();
                    return productInputPort.save(product, branchName, franchiseName)
                            .map(productDTOMapper::toResponse);
                })
                .flatMap(response -> created("Producto creado con exito", response));
    }

    public Mono<ServerResponse> listenUpdateProduct(ServerRequest serverRequest) {
        return bodyToMonoValidated(validator, serverRequest, CreateProductDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getId() == null || dto.getId().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("El id del producto es obligatorio para actualizar un producto"));
                    }
                    return Mono.just(dto);
                })
                .map(productDTOMapper::toModel)
                .flatMap(productInputPort::update)
                .map(productDTOMapper::toResponse)
                .flatMap(response -> ok("Producto actualizado con exito", response));
    }

    public Mono<ServerResponse> listenDeleteProduct(ServerRequest serverRequest) {
        String productName = serverRequest.queryParam("id")
                .orElseThrow(() -> new IllegalArgumentException("El id del producto es obligatorio"));
        return productInputPort.delete(productName)
                .then(ok("Producto eliminado con exito"));

    }

    public Mono<ServerResponse> listenUpdateStockProduct(ServerRequest serverRequest) {
        String productId = serverRequest.queryParam("id")
                .orElseThrow(() -> new IllegalArgumentException("El id del producto es obligatorio"));
        return bodyToMonoValidated(validator, serverRequest, UpdateStockProductRequestDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(BODY_EMPTY_ERROR)))
                .flatMap(dto -> {
                    if (dto.getStock() == null || dto.getStock() < 0) {
                        return Mono.error(new IllegalArgumentException("El stock del producto es obligatorio y debe ser mayor o igual a cero"));
                    }
                    return productInputPort.updateStock(productId, dto.getStock())
                            .map(productDTOMapper::toResponse);
                })
                .flatMap(response -> ok("Stock actualizado con exito", response));
    }

    public Mono<ServerResponse> listenGetTopProductsPerBranch(ServerRequest serverRequest) {
        return Mono.just(serverRequest.pathVariable("franchiseName"))
                .flatMapMany(productInputPort::getTopProductPerBranch)
                .collectList()
                .flatMap(result -> ok("Consulta exitosa", result));
    }
}

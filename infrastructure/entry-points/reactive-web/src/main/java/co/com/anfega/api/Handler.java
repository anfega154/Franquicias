package co.com.anfega.api;

import co.com.anfega.api.dto.CreateBranchDTO;
import co.com.anfega.api.dto.CreateFranchiseDTO;
import co.com.anfega.api.dto.CreateProductDTO;
import co.com.anfega.api.dto.FranchiseDTO;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.mapper.BranchDTOMapper;
import co.com.anfega.api.mapper.FranchiseDTOMapper;
import co.com.anfega.api.mapper.ProductDTOMapper;
import co.com.anfega.model.branch.gateways.BranchInputPort;
import co.com.anfega.model.franchise.gateways.FranchiseInputPort;
import co.com.anfega.model.product.gateways.ProductInputPort;
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


    public Mono<ServerResponse> listenSaveFranchise(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateFranchiseDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El body no puede estar vacío")))
                .map(franchiseDTOMapper::toModel)
                .flatMap(franchiseInputPort::save)
                .map(franchiseDTOMapper::toResponse)
                .flatMap(response -> created("Franquicia creada con exito", response));
    }

    public Mono<ServerResponse> listenSaveBranch(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateBranchDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El body no puede estar vacío")))
                .flatMap(dto -> {
                    var branch = branchDTOMapper.toModel(dto);
                    var franchiseName = dto.getFranchiseName();
                    return branchInputPort.save(branch, franchiseName)
                            .map(branchDTOMapper::toResponse);
                })
                .flatMap(response -> created("Sucursal creada con exito", response));
    }

    public Mono<ServerResponse> listenSaveProduct(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateProductDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El body no puede estar vacío")))
                .flatMap(dto -> {
                    var product = productDTOMapper.toModel(dto);
                    var branchName = dto.getBranchName();
                    return productInputPort.save(product, branchName)
                            .map(productDTOMapper::toResponse);
                })
                .flatMap(response -> created("Producto creado con exito", response));
    }
}

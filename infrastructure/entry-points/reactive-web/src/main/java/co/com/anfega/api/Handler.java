package co.com.anfega.api;

import co.com.anfega.api.dto.CreateFranchiseDTO;
import co.com.anfega.api.dto.FranchiseDTO;
import co.com.anfega.api.helper.api.BaseHandler;
import co.com.anfega.api.mapper.FranchiseDTOMapper;
import co.com.anfega.model.franchise.gateways.FranchiseInputPort;
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


    public Mono<ServerResponse> listenSaveFranchise(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateFranchiseDTO.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El body no puede estar vacío")))
                .map(franchiseDTOMapper::toModel)
                .flatMap(franchiseInputPort::save)
                .map(franchiseDTOMapper::toResponse)
                .flatMap(response -> created("Franquicia creada con exito", response))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage())
                );
    }
}

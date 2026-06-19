package co.com.anfega.api;

import co.com.anfega.api.config.FranchisePath;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RouterRestTest {

    @Test
    void shouldRouteCreateFranchiseRequestsToHandler() {
        FranchisePath franchisePath = new FranchisePath();
        franchisePath.setFranchises("/api/v1/franquicias");
        franchisePath.setBranches("/api/v1/sucursales");
        franchisePath.setProducts("/api/v1/productos");
        franchisePath.setProductsUpdateStock("/api/v1/productos/update-stock");
        franchisePath.setTopProducts("/api/v1/franchises/{franchiseName}/top-products-per-branch");

        Handler handler = mock(Handler.class);
        when(handler.listenSaveFranchise(any())).thenReturn(ServerResponse.ok().build());

        WebTestClient.bindToRouterFunction(new RouterRest(franchisePath).routerFunction(handler))
                .build()
                .post()
                .uri("/api/v1/franquicias")
                .exchange()
                .expectStatus()
                .isOk();

        verify(handler).listenSaveFranchise(any());
    }
}

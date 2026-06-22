package co.com.anfega.api;

import co.com.anfega.api.dto.CreateBranchDTO;
import co.com.anfega.api.dto.CreateFranchiseDTO;
import co.com.anfega.api.dto.CreateProductDTO;
import co.com.anfega.api.dto.UpdateStockProductRequestDTO;
import co.com.anfega.api.mapper.BranchDTOMapperImpl;
import co.com.anfega.api.mapper.FranchiseDTOMapperImpl;
import co.com.anfega.api.mapper.ProductDTOMapperImpl;
import co.com.anfega.model.branch.Branch;
import co.com.anfega.model.branch.gateways.BranchInputPort;
import co.com.anfega.model.common.constants.Constants;
import co.com.anfega.model.common.error.BusinessException;
import co.com.anfega.model.common.error.ErrorCode;
import co.com.anfega.model.franchise.Franchise;
import co.com.anfega.model.franchise.gateways.FranchiseInputPort;
import co.com.anfega.model.product.Product;
import co.com.anfega.model.product.gateways.ProductInputPort;
import co.com.anfega.model.topproductperbranch.TopProductPerBranch;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest.Headers;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HandlerTest {

    private static final String TRACE_ID = "123e4567-e89b-12d3-a456-426614174000";

    @Mock
    private FranchiseInputPort franchiseInputPort;
    @Mock
    private BranchInputPort branchInputPort;
    @Mock
    private ProductInputPort productInputPort;

    private Handler handler;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        handler = new Handler(
                new FranchiseDTOMapperImpl(),
                franchiseInputPort,
                new BranchDTOMapperImpl(),
                branchInputPort,
                productInputPort,
                new ProductDTOMapperImpl(),
                validator
        );
    }

    @Test
    void shouldSaveFranchise() {
        ServerRequest request = requestWithBody(franchiseRequest(null, "Franquicia Uno"));
        Franchise saved = new Franchise("fr-1", "Franquicia Uno", List.of());
        when(franchiseInputPort.save(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(handler.listenSaveFranchise(request))
                .assertNext(response -> assertEquals(HttpStatus.CREATED, response.statusCode()))
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseInputPort).save(captor.capture());
        assertEquals("Franquicia Uno", captor.getValue().getName());
    }

    @Test
    void shouldRejectCreateFranchiseWhenIdIsPresent() {
        ServerRequest request = requestWithBody(franchiseRequest("fr-1", "Franquicia Uno"));

        StepVerifier.create(handler.listenSaveFranchise(request))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessException);
                    assertEquals(Constants.FRANCHISE_ID_MUST_BE_EMPTY, error.getMessage());
                })
                .verify();

        verifyNoInteractions(franchiseInputPort);
    }

    @Test
    void shouldUpdateFranchise() {
        ServerRequest request = requestWithBody(franchiseRequest("fr-1", "Franquicia Uno"));
        Franchise saved = new Franchise("fr-1", "Franquicia Dos", List.of());
        when(franchiseInputPort.update(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(handler.listenUpdateFranchise(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();

        verify(franchiseInputPort).update(any(Franchise.class));
    }

    @Test
    void shouldRejectUpdateFranchiseWhenIdIsMissing() {
        ServerRequest request = requestWithBody(franchiseRequest(null, "Franquicia Uno"));

        StepVerifier.create(handler.listenUpdateFranchise(request))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessException);
                    assertEquals(Constants.FRANCHISE_ID_REQUIRED, error.getMessage());
                })
                .verify();
    }

    @Test
    void shouldSaveBranch() {
        ServerRequest request = requestWithBody(branchRequest(null, "Sucursal Centro", "Franquicia Uno"));
        Branch saved = new Branch("br-1", "Sucursal Centro");
        when(branchInputPort.save(any(Branch.class), anyString())).thenReturn(Mono.just(saved));

        StepVerifier.create(handler.listenSaveBranch(request))
                .assertNext(response -> assertEquals(HttpStatus.CREATED, response.statusCode()))
                .verifyComplete();

        verify(branchInputPort).save(any(Branch.class), anyString());
    }

    @Test
    void shouldUpdateBranch() {
        ServerRequest request = requestWithBody(branchRequest("br-1", "Sucursal Centro", "Franquicia Uno"));
        Branch saved = new Branch("br-1", "Sucursal Norte");
        when(branchInputPort.update(any(Branch.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(handler.listenUpdateBranch(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();

        verify(branchInputPort).update(any(Branch.class));
    }

    @Test
    void shouldSaveProduct() {
        ServerRequest request = requestWithBody(productRequest(null, "Producto A", 8L, "Sucursal Centro", "Franquicia Uno"));
        Product saved = new Product("pd-1", "Producto A", 8L);
        when(productInputPort.save(any(Product.class), anyString(), anyString())).thenReturn(Mono.just(saved));

        StepVerifier.create(handler.listenSaveProduct(request))
                .assertNext(response -> assertEquals(HttpStatus.CREATED, response.statusCode()))
                .verifyComplete();

        verify(productInputPort).save(any(Product.class), anyString(), anyString());
    }

    @Test
    void shouldUpdateProduct() {
        ServerRequest request = requestWithBody(productRequest("pd-1", "Producto B", 9L, "Sucursal Centro", "Franquicia Uno"));
        Product saved = new Product("pd-1", "Producto B", 9L);
        when(productInputPort.update(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(handler.listenUpdateProduct(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();

        verify(productInputPort).update(any(Product.class));
    }

    @Test
    void shouldDeleteProduct() {
        ServerRequest request = requestWithQueryParam("pd-1");
        when(productInputPort.delete("pd-1")).thenReturn(Mono.empty());

        StepVerifier.create(handler.listenDeleteProduct(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();

        verify(productInputPort).delete("pd-1");
    }

    @Test
    void shouldRejectDeleteProductWhenQueryParamIsMissing() {
        ServerRequest request = baseRequest();
        when(request.queryParam(Constants.QUERY_PARAM_ID)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> handler.listenDeleteProduct(request));
        assertEquals(Constants.PRODUCT_QUERY_ID_REQUIRED, exception.getMessage());
    }

    @Test
    void shouldUpdateProductStock() {
        ServerRequest request = requestWithStock("pd-1", 12L);
        Product updated = new Product("pd-1", "Producto B", 12L);
        when(productInputPort.updateStock("pd-1", 12L)).thenReturn(Mono.just(updated));

        StepVerifier.create(handler.listenUpdateStockProduct(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();

        verify(productInputPort).updateStock("pd-1", 12L);
    }

    @Test
    void shouldRejectProductStockWhenBodyIsEmpty() {
        ServerRequest request = requestWithQueryParam("pd-1");
        when(request.bodyToMono(UpdateStockProductRequestDTO.class)).thenReturn(Mono.empty());

        StepVerifier.create(handler.listenUpdateStockProduct(request))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessException);
                    assertEquals(Constants.BODY_EMPTY_ERROR, error.getMessage());
                })
                .verify();
    }

    @Test
    void shouldRejectProductStockWhenValueIsNegative() {
        ServerRequest request = requestWithStock("pd-1", -1L);
        Validator permissiveValidator = mock(Validator.class);
        when(permissiveValidator.validate(any(UpdateStockProductRequestDTO.class))).thenReturn(java.util.Collections.emptySet());
        Handler permissiveHandler = new Handler(
                new FranchiseDTOMapperImpl(),
                franchiseInputPort,
                new BranchDTOMapperImpl(),
                branchInputPort,
                productInputPort,
                new ProductDTOMapperImpl(),
                permissiveValidator
        );

        StepVerifier.create(permissiveHandler.listenUpdateStockProduct(request))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessException);
                    assertEquals(ErrorCode.VALIDATION_ERROR, ((BusinessException) error).getErrorCode());
                })
                .verify();

        verifyNoInteractions(productInputPort);
    }

    @Test
    void shouldGetTopProductsPerBranch() {
        ServerRequest request = baseRequest();
        when(request.pathVariable("franchiseName")).thenReturn("Franquicia Uno");
        when(productInputPort.getTopProductPerBranch("Franquicia Uno")).thenReturn(
                Flux.just(new TopProductPerBranch("br-1", "Sucursal Centro", "pd-1", "Producto A", 12L))
        );

        StepVerifier.create(handler.listenGetTopProductsPerBranch(request))
                .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                .verifyComplete();

        verify(productInputPort).getTopProductPerBranch("Franquicia Uno");
    }

    @Test
    void shouldRejectGetTopProductsWhenFranchiseNameIsEmpty() {
        ServerRequest request = baseRequest();
        when(request.pathVariable("franchiseName")).thenReturn("");

        StepVerifier.create(handler.listenGetTopProductsPerBranch(request))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BusinessException);
                    assertEquals(ErrorCode.VALIDATION_ERROR, ((BusinessException) error).getErrorCode());
                })
                .verify();
    }

    @Test
    void shouldFailValidationWhenBodyContainsBlankFields() {
        ServerRequest request = requestWithBody(franchiseRequest(null, ""));

        StepVerifier.create(handler.listenSaveFranchise(request))
                .expectErrorMatches(error -> error instanceof jakarta.validation.ConstraintViolationException)
                .verify();
    }

    private ServerRequest requestWithBody(CreateFranchiseDTO dto) {
        ServerRequest request = baseRequest();
        when(request.bodyToMono(CreateFranchiseDTO.class)).thenReturn(Mono.just(dto));
        return request;
    }

    private ServerRequest requestWithBody(CreateBranchDTO dto) {
        ServerRequest request = baseRequest();
        when(request.bodyToMono(CreateBranchDTO.class)).thenReturn(Mono.just(dto));
        return request;
    }

    private ServerRequest requestWithBody(CreateProductDTO dto) {
        ServerRequest request = baseRequest();
        when(request.bodyToMono(CreateProductDTO.class)).thenReturn(Mono.just(dto));
        return request;
    }

    private ServerRequest requestWithStock(String productId, Long stock) {
        ServerRequest request = requestWithQueryParam(productId);
        UpdateStockProductRequestDTO dto = new UpdateStockProductRequestDTO();
        dto.setStock(stock);
        when(request.bodyToMono(UpdateStockProductRequestDTO.class)).thenReturn(Mono.just(dto));
        return request;
    }

    private ServerRequest requestWithQueryParam(String productId) {
        ServerRequest request = baseRequest();
        when(request.queryParam(Constants.QUERY_PARAM_ID)).thenReturn(Optional.of(productId));
        return request;
    }

    private ServerRequest baseRequest() {
        ServerRequest request = mock(ServerRequest.class);
        Headers headers = mock(Headers.class);
        when(request.attribute(Constants.TRACE_ID_ATTRIBUTE)).thenReturn(Optional.of(TRACE_ID));
        when(request.headers()).thenReturn(headers);
        when(headers.firstHeader(Constants.TRACE_ID_HEADER)).thenReturn(TRACE_ID);
        return request;
    }

    private CreateFranchiseDTO franchiseRequest(String id, String name) {
        CreateFranchiseDTO dto = new CreateFranchiseDTO();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    private CreateBranchDTO branchRequest(String id, String name, String franchiseName) {
        CreateBranchDTO dto = new CreateBranchDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setFranchiseName(franchiseName);
        return dto;
    }

    private CreateProductDTO productRequest(String id, String name, Long stock, String branchName, String franchiseName) {
        CreateProductDTO dto = new CreateProductDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setStock(stock);
        dto.setBranchName(branchName);
        dto.setFranchiseName(franchiseName);
        return dto;
    }
}

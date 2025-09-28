package co.com.anfega.api;

import co.com.anfega.api.config.FranchisePath;
import co.com.anfega.api.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Franchise API", description = "Operaciones para gestionar franquicias, sucursales y productos")
public class RouterRest {

    private final FranchisePath franchisePath;

    public RouterRest(FranchisePath franchisePath) {
        this.franchisePath = franchisePath;
    }

    @Bean
    @RouterOperations({

            @RouterOperation(
                    path = "/api/v1/franquicias",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Crear franquicia",
                            description = "Crea una nueva franquicia en el sistema",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Información de la franquicia a crear",
                                    content = @Content(
                                            schema = @Schema(implementation = CreateFranchiseDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Franquicia creada exitosamente",
                                            content = @Content(
                                                    schema = @Schema(implementation = FranchiseDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación en los datos enviados",
                                            content = @Content(schema = @Schema(example = """
                                                    {
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "El nombre de la franquicia es obligatorio",
                                                      "path": "/api/v1/franquicias"
                                                    }
                                                    """))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franquicias",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "listenUpdateFranchise",
                    operation = @Operation(
                            operationId = "UpdateFranchise",
                            summary = "Actualizar franquicia",
                            description = "Actualiza la información de una franquicia existente",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Información de la franquicia a actualizar",
                                    content = @Content(
                                            schema = @Schema(implementation = CreateFranchiseDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Franquicia actualizada con exito",
                                            content = @Content(
                                                    schema = @Schema(implementation = FranchiseDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación en los datos enviados",
                                            content = @Content(schema = @Schema(example = """
                                                    {
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "El nombre de la franquicia es obligatorio",
                                                      "path": "/api/v1/franquicias"
                                                    }
                                                    """))
                                    )
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/sucursales",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveBranch",
                    operation = @Operation(
                            operationId = "createBranch",
                            summary = "Crear sucursal",
                            description = "Crea una nueva sucursal asociada a una franquicia",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Información de la sucursal a crear",
                                    content = @Content(
                                            schema = @Schema(implementation = CreateBranchDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Sucursal creada exitosamente",
                                            content = @Content(
                                                    schema = @Schema(implementation = BranchDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación",
                                            content = @Content(schema = @Schema(example = """
                                                    {
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "El nombre de la sucursal es obligatorio",
                                                      "path": "/api/v1/sucursales"
                                                    }
                                                    """))
                                    )
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/productos",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Crear producto",
                            description = "Crea un nuevo producto en una sucursal",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Información del producto a crear",
                                    content = @Content(
                                            schema = @Schema(implementation = CreateProductDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Producto creado exitosamente",
                                            content = @Content(
                                                    schema = @Schema(implementation = ProductDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación",
                                            content = @Content(schema = @Schema(example = """
                                                    {
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "El nombre del producto es obligatorio",
                                                      "path": "/api/v1/productos"
                                                    }
                                                    """))
                                    )
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/productos",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "listenUpdateStockProduct",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Actualizar stock de producto",
                            description = "Actualiza la cantidad de stock de un producto",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Información para actualizar el stock",
                                    content = @Content(
                                            schema = @Schema(implementation = UpdateStockProductRequestDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Producto actualizado correctamente",
                                            content = @Content(
                                                    schema = @Schema(implementation = ProductDTO.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación"
                                    )
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/productos",
                    produces = {"application/json"},
                    consumes = {"application/json"},
                    method = RequestMethod.DELETE,
                    beanClass = Handler.class,
                    beanMethod = "listenDeleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Eliminar producto",
                            description = "Elimina un producto dado su ID",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Producto a eliminar",
                                    content = @Content(
                                            schema = @Schema(implementation = ProductDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Producto eliminado"),
                                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
                            }
                    )
            ),

            @RouterOperation(
                    path = "/api/v1/franchises/{franchiseName}/top-products-per-branch",
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenGetTopProductsPerBranch",
                    operation = @Operation(
                            operationId = "getTopProductsPerBranch",
                            summary = "Obtener producto con mayor stock por sucursal",
                            parameters = {
                                    @Parameter(
                                            name = "franchiseName",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Nombre de la franquicia"
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de productos con mayor stock por sucursal",
                                            content = @Content(
                                                    array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                                            )
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(franchisePath.getFranchises()), handler::listenSaveFranchise)
                .andRoute(PUT(franchisePath.getFranchises()), handler::listenUpdateFranchise)
                .andRoute(POST(franchisePath.getBranches()), handler::listenSaveBranch)
                .andRoute(POST(franchisePath.getProducts()), handler::listenSaveProduct)
                .andRoute(PUT(franchisePath.getProducts()), handler::listenUpdateStockProduct)
                .andRoute(DELETE(franchisePath.getProducts()), handler::listenDeleteProduct)
                .andRoute(GET(franchisePath.getTopProducts()), handler::listenGetTopProductsPerBranch);
    }
}

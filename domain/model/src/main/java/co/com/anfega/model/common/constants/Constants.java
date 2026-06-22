package co.com.anfega.model.common.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public static final String TRACE_ID_HEADER = "X-B3-TraceId";
    public static final String TRACE_ID_COMPATIBILITY_HEADER = "X-Trace-Id";
    public static final String TRACE_ID_ATTRIBUTE = "traceId";
    public static final String QUERY_PARAM_ID = "id";
    public static final String UUID_REGEX =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

    public static final String SUCCESS_CODE = "FRQ_SUCC_0001";

    public static final String DEFAULT_ERROR = "Internal Server Error";
    public static final String DEFAULT_ERROR_MESSAGE = "Ha ocurrido un error inesperado";
    public static final String VALIDATION_ERROR = "Bad Request";
    public static final String RESOURCE_NOT_FOUND_ERROR = "Not Found";
    public static final String SERVICE_UNAVAILABLE_ERROR = "Service Unavailable";
    public static final String CONFLICT_ERROR = "Conflict";

    public static final String BODY_EMPTY_ERROR = "El body no puede estar vacío";
    public static final String INVALID_TRACE_ID_MESSAGE = "El header X-B3-TraceId debe ser un UUID válido";
    public static final String INVALID_JSON_MESSAGE = "El body debe tener un JSON válido";
    public static final String FRANCHISE_ID_MUST_BE_EMPTY = "No envíes el id al crear una franquicia";
    public static final String FRANCHISE_ID_REQUIRED = "Debes enviar el id para actualizar la franquicia";
    public static final String BRANCH_ID_MUST_BE_EMPTY = "No envíes el id al crear una sucursal";
    public static final String BRANCH_ID_REQUIRED = "Debes enviar el id para actualizar la sucursal";
    public static final String PRODUCT_ID_MUST_BE_EMPTY = "No envíes el id al crear un producto";
    public static final String PRODUCT_ID_REQUIRED = "Debes enviar el id para actualizar el producto";
    public static final String PRODUCT_QUERY_ID_REQUIRED = "El id del producto es obligatorio";
    public static final String PRODUCT_STOCK_REQUIRED = "El stock del producto es obligatorio y debe ser mayor o igual a cero";
    public static final String FRANCHISE_NAME_REQUIRED = "El nombre de la franquicia es obligatorio";

    public static final String CREATE_FRANCHISE_SUCCESS_MESSAGE = "Franquicia creada con éxito";
    public static final String UPDATE_FRANCHISE_SUCCESS_MESSAGE = "Franquicia actualizada con éxito";
    public static final String CREATE_BRANCH_SUCCESS_MESSAGE = "Sucursal creada con éxito";
    public static final String UPDATE_BRANCH_SUCCESS_MESSAGE = "Sucursal actualizada con éxito";
    public static final String CREATE_PRODUCT_SUCCESS_MESSAGE = "Producto creado con éxito";
    public static final String UPDATE_PRODUCT_SUCCESS_MESSAGE = "Producto actualizado con éxito";
    public static final String DELETE_PRODUCT_SUCCESS_MESSAGE = "Producto eliminado con éxito";
    public static final String UPDATE_PRODUCT_STOCK_SUCCESS_MESSAGE = "Stock actualizado con éxito";
    public static final String QUERY_SUCCESS_MESSAGE = "Consulta exitosa";

    public static final String FRANCHISE_NAME_REQUIRED_VALIDATION_MESSAGE = "El nombre de la franquicia es obligatorio";
    public static final String BRANCH_NAME_REQUIRED_VALIDATION_MESSAGE = "El nombre de la sucursal es obligatorio";
    public static final String PRODUCT_NAME_REQUIRED_VALIDATION_MESSAGE = "El nombre del producto es obligatorio";
    public static final String PRODUCT_STOCK_REQUIRED_VALIDATION_MESSAGE = "El stock del producto es obligatorio";
    public static final String PRODUCT_STOCK_POSITIVE_OR_ZERO_VALIDATION_MESSAGE = "El stock del producto debe ser mayor o igual a cero";

    public static final String LOG_CREATE_FRANCHISE_START = "Iniciando creación de franquicia. traceId={}";
    public static final String LOG_CREATE_FRANCHISE_SUCCESS = "Franquicia creada exitosamente. franchiseId={}, traceId={}";
    public static final String LOG_UPDATE_FRANCHISE_START = "Iniciando actualización de franquicia. traceId={}";
    public static final String LOG_UPDATE_FRANCHISE_SUCCESS = "Franquicia actualizada exitosamente. franchiseId={}, traceId={}";
    public static final String LOG_CREATE_BRANCH_START = "Iniciando creación de sucursal. traceId={}";
    public static final String LOG_CREATE_BRANCH_SUCCESS = "Sucursal creada exitosamente. branchId={}, traceId={}";
    public static final String LOG_UPDATE_BRANCH_START = "Iniciando actualización de sucursal. traceId={}";
    public static final String LOG_UPDATE_BRANCH_SUCCESS = "Sucursal actualizada exitosamente. branchId={}, traceId={}";
    public static final String LOG_CREATE_PRODUCT_START = "Iniciando creación de producto. traceId={}";
    public static final String LOG_CREATE_PRODUCT_SUCCESS = "Producto creado exitosamente. productId={}, traceId={}";
    public static final String LOG_UPDATE_PRODUCT_START = "Iniciando actualización de producto. traceId={}";
    public static final String LOG_UPDATE_PRODUCT_SUCCESS = "Producto actualizado exitosamente. productId={}, traceId={}";
    public static final String LOG_DELETE_PRODUCT_START = "Iniciando eliminación de producto. productId={}, traceId={}";
    public static final String LOG_DELETE_PRODUCT_SUCCESS = "Producto eliminado exitosamente. productId={}, traceId={}";
    public static final String LOG_UPDATE_STOCK_START = "Iniciando actualización de stock. productId={}, traceId={}";
    public static final String LOG_UPDATE_STOCK_SUCCESS = "Stock actualizado exitosamente. productId={}, traceId={}";
    public static final String LOG_GET_TOP_PRODUCTS_START = "Iniciando consulta de productos top por sucursal. franchiseName={}, traceId={}";
    public static final String LOG_GET_TOP_PRODUCTS_SUCCESS = "Consulta de productos top completada. totalResults={}, traceId={}";
    public static final String LOG_REQUEST_ERROR = "Error procesando la solicitud. traceId={}, error={}";
    public static final String LOG_RESPONSE_SUCCESS = "Respuesta enviada correctamente. traceId={}, message={}";
    public static final String LOG_VALIDATION_ERRORS = "Errores de validación detectados. traceId={}, errors={}";
    public static final String LOG_EXCEPTION_RESOLVED = "Excepción resuelta. traceId={}, status={}, code={}, message={}";
    public static final String LOG_UNEXPECTED_EXCEPTION = "Excepción inesperada. traceId={}";

    public static final String LOG_USE_CASE_SAVE_FRANCHISE_START = "UseCase guardar franquicia. franchiseName=%s";
    public static final String LOG_USE_CASE_SAVE_FRANCHISE_SUCCESS = "UseCase guardar franquicia completado. franchiseId=%s";
    public static final String LOG_USE_CASE_UPDATE_FRANCHISE_START = "UseCase actualizar franquicia. franchiseId=%s";
    public static final String LOG_USE_CASE_UPDATE_FRANCHISE_SUCCESS = "UseCase actualizar franquicia completado. franchiseId=%s";
    public static final String LOG_USE_CASE_SAVE_BRANCH_START = "UseCase guardar sucursal. branchName=%s, franchiseName=%s";
    public static final String LOG_USE_CASE_SAVE_BRANCH_SUCCESS = "UseCase guardar sucursal completado. branchId=%s";
    public static final String LOG_USE_CASE_UPDATE_BRANCH_START = "UseCase actualizar sucursal. branchId=%s";
    public static final String LOG_USE_CASE_UPDATE_BRANCH_SUCCESS = "UseCase actualizar sucursal completado. branchId=%s";
    public static final String LOG_USE_CASE_SAVE_PRODUCT_START = "UseCase guardar producto. productName=%s, branchName=%s, franchiseName=%s";
    public static final String LOG_USE_CASE_SAVE_PRODUCT_SUCCESS = "UseCase guardar producto completado. productId=%s";
    public static final String LOG_USE_CASE_DELETE_PRODUCT_START = "UseCase eliminar producto. productId=%s";
    public static final String LOG_USE_CASE_DELETE_PRODUCT_SUCCESS = "UseCase eliminar producto completado. productId=%s";
    public static final String LOG_USE_CASE_UPDATE_STOCK_START = "UseCase actualizar stock. productId=%s, stock=%s";
    public static final String LOG_USE_CASE_UPDATE_STOCK_SUCCESS = "UseCase actualizar stock completado. productId=%s, stock=%s";
    public static final String LOG_USE_CASE_GET_TOP_PRODUCTS_START = "UseCase consultar top productos. franchiseName=%s";
    public static final String LOG_USE_CASE_GET_TOP_PRODUCTS_SUCCESS = "UseCase consultar top productos completado. branchId=%s, productId=%s";
    public static final String LOG_USE_CASE_UPDATE_PRODUCT_START = "UseCase actualizar producto. productId=%s";
    public static final String LOG_USE_CASE_UPDATE_PRODUCT_SUCCESS = "UseCase actualizar producto completado. productId=%s";

    public static final String LOG_PERSISTENCE_SAVE_FRANCHISE_START = "Persistence adapter guardando franquicia. franchiseName={}";
    public static final String LOG_PERSISTENCE_SAVE_FRANCHISE_SUCCESS = "Persistence adapter franquicia guardada. franchiseId={}";
    public static final String LOG_PERSISTENCE_FIND_FRANCHISE_BY_NAME_START = "Persistence adapter buscando franquicia por nombre. franchiseName={}";
    public static final String LOG_PERSISTENCE_UPDATE_FRANCHISE_START = "Persistence adapter actualizando franquicia. franchiseId={}";
    public static final String LOG_PERSISTENCE_SAVE_BRANCH_START = "Persistence adapter guardando sucursal. branchName={}, franchiseId={}";
    public static final String LOG_PERSISTENCE_FIND_BRANCH_BY_NAME_START = "Persistence adapter buscando sucursal. branchName={}, franchiseId={}";
    public static final String LOG_PERSISTENCE_FIND_BRANCHES_BY_FRANCHISE_ID_START = "Persistence adapter buscando sucursales por franquicia. franchiseId={}";
    public static final String LOG_PERSISTENCE_UPDATE_BRANCH_START = "Persistence adapter actualizando sucursal. branchId={}";
    public static final String LOG_PERSISTENCE_SAVE_PRODUCT_START = "Persistence adapter guardando producto. productName={}, branchId={}";
    public static final String LOG_PERSISTENCE_DELETE_PRODUCT_START = "Persistence adapter eliminando producto. productId={}";
    public static final String LOG_PERSISTENCE_UPDATE_PRODUCT_STOCK_START = "Persistence adapter actualizando stock. productId={}, stock={}";
    public static final String LOG_PERSISTENCE_FIND_TOP_PRODUCT_START = "Persistence adapter buscando producto top por sucursal. branchId={}";
    public static final String LOG_PERSISTENCE_UPDATE_PRODUCT_START = "Persistence adapter actualizando producto. productId={}";
    public static final String LOG_PERSISTENCE_OPERATION_TIMEOUT = "Mongo operation timed out while {}";
    public static final String LOG_PERSISTENCE_CIRCUIT_OPEN = "Mongo circuit breaker is open while {}";
    public static final String LOG_PERSISTENCE_CONNECTIVITY_FAILURE = "Mongo connectivity failure while {}";
    public static final String LOG_PERSISTENCE_OPERATION_FAILURE = "Mongo operation failed while {}";

    public static final String PERSISTENCE_TIMEOUT_MESSAGE = "La persistencia tardó demasiado en responder";
    public static final String PERSISTENCE_TEMPORARILY_UNAVAILABLE_MESSAGE = "La persistencia no está disponible temporalmente";
    public static final String PERSISTENCE_UNAVAILABLE_MESSAGE = "La persistencia no está disponible";
    public static final String PERSISTENCE_OPERATION_FAILED_MESSAGE = "Falló la operación en la persistencia";

    public static final String CONTENT_SECURITY_POLICY_HEADER = "Content-Security-Policy";
    public static final String CONTENT_SECURITY_POLICY_VALUE = "default-src 'self'; frame-ancestors 'self'; form-action 'self'";
    public static final String STRICT_TRANSPORT_SECURITY_HEADER = "Strict-Transport-Security";
    public static final String STRICT_TRANSPORT_SECURITY_VALUE = "max-age=31536000;";
    public static final String X_CONTENT_TYPE_OPTIONS_HEADER = "X-Content-Type-Options";
    public static final String X_CONTENT_TYPE_OPTIONS_VALUE = "nosniff";
    public static final String CACHE_CONTROL_HEADER = "Cache-Control";
    public static final String CACHE_CONTROL_VALUE = "no-store";
    public static final String PRAGMA_HEADER = "Pragma";
    public static final String PRAGMA_VALUE = "no-cache";
    public static final String REFERRER_POLICY_HEADER = "Referrer-Policy";
    public static final String REFERRER_POLICY_VALUE = "strict-origin-when-cross-origin";

    public static final String REQUEST_METHOD_POST = "POST";
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_PUT = "PUT";
    public static final String REQUEST_METHOD_DELETE = "DELETE";
    public static final String REQUEST_METHOD_OPTIONS = "OPTIONS";
}

package co.com.anfega.model.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_ERROR("FRQ_VAL_0001", "Error de validación en los datos de entrada", 400),
    INVALID_JSON("FRQ_VAL_0002", "Formato JSON inválido en la petición", 400),
    INVALID_TRACE_ID("FRQ_VAL_0003", "El header X-B3-TraceId debe ser un UUID válido", 400),
    FRANCHISE_NOT_FOUND("FRQ_NOTF_0001", "La franquicia con nombre %s no existe.", 404),
    BRANCH_NOT_FOUND("FRQ_NOTF_0002", "La sucursal con nombre %s no existe.", 404),
    PRODUCT_NOT_FOUND("FRQ_NOTF_0003", "El producto no fue encontrado", 404),
    INTERNAL_ERROR("FRQ_INT_0001", "Ha ocurrido un error inesperado", 500),
    DATABASE_ERROR("FRQ_INT_0002", "Error al consultar la base de datos", 500),
    PERSISTENCE_UNAVAILABLE("FRQ_INT_0003", "El servicio de persistencia no está disponible temporalmente", 503);

    private final String code;
    private final String message;
    private final int traditionalStatusCode;
}

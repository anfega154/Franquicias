package co.com.anfega.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateProductDTO {
    @Schema(description = "nombre del producto", example = "Producto A")
    private String name;
    @Schema(description = "stock del producto", example = "100")
    private Long stock;
    @Schema(description = "nombre de la sucursal", example = "Sucursal Central")
    private String branchName;
    @Schema(description = "nombre de la franquicia", example = "Franquicia XYZ")
    private String franchiseName;
}

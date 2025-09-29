package co.com.anfega.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateProductDTO {
    private String id;
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;
    @NotBlank(message = "El stock del producto es obligatorio")
    @Positive(message = "El stock del producto debe ser mayor a cero")
    private Long stock;
    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    private String branchName;
    @NotBlank(message = "El nombre de la franquicia es obligatorio")
    private String franchiseName;
}

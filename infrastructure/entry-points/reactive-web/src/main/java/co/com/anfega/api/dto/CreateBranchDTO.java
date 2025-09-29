package co.com.anfega.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBranchDTO {
    private String id;
    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    private String name;
    @NotBlank(message = "El nombre de la franquicia es obligatorio")
    private String franchiseName;
}

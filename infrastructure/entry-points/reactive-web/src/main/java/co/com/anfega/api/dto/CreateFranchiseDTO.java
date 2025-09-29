package co.com.anfega.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFranchiseDTO {
    private String id;
    @NotBlank(message = "El nombre de la franquicia es obligatorio")
    private String name;
}

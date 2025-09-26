package co.com.anfega.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class CreateFranchiseDTO {
    private String name;
}

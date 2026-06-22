package co.com.anfega.api.dto;

import co.com.anfega.model.common.constants.Constants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateFranchiseDTO {
    private String id;
    @NotBlank(message = Constants.FRANCHISE_NAME_REQUIRED_VALIDATION_MESSAGE)
    private String name;
}

package co.com.anfega.api.dto;

import co.com.anfega.model.common.constants.Constants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBranchDTO {
    private String id;
    @NotBlank(message = Constants.BRANCH_NAME_REQUIRED_VALIDATION_MESSAGE)
    private String name;
    @NotBlank(message = Constants.FRANCHISE_NAME_REQUIRED_VALIDATION_MESSAGE)
    private String franchiseName;
}

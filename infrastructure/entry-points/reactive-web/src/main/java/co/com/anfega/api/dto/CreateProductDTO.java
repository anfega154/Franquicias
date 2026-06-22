package co.com.anfega.api.dto;

import co.com.anfega.model.common.constants.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateProductDTO {
    private String id;
    @NotBlank(message = Constants.PRODUCT_NAME_REQUIRED_VALIDATION_MESSAGE)
    private String name;
    @NotNull(message = Constants.PRODUCT_STOCK_REQUIRED_VALIDATION_MESSAGE)
    @PositiveOrZero(message = Constants.PRODUCT_STOCK_POSITIVE_OR_ZERO_VALIDATION_MESSAGE)
    private Long stock;
    @NotBlank(message = Constants.BRANCH_NAME_REQUIRED_VALIDATION_MESSAGE)
    private String branchName;
    @NotBlank(message = Constants.FRANCHISE_NAME_REQUIRED_VALIDATION_MESSAGE)
    private String franchiseName;
}

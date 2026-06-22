package co.com.anfega.api.dto;

import co.com.anfega.model.common.constants.Constants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateStockProductRequestDTO {
    @NotNull(message = Constants.PRODUCT_STOCK_REQUIRED_VALIDATION_MESSAGE)
    @PositiveOrZero(message = Constants.PRODUCT_STOCK_POSITIVE_OR_ZERO_VALIDATION_MESSAGE)
    private Long stock;
}

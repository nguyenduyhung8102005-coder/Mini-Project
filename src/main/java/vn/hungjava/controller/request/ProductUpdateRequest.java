package vn.hungjava.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import vn.hungjava.common.ProductStatus;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {
    @NotNull(message = "Id must not be null")
    @Positive(message = "Id must be greater than 0")
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    private String description;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock must not be null")
    @PositiveOrZero(
            message = "Stock must be greater than or equal to 0"
    )
    private Integer stock;

    private String sku;

    @NotNull(message = "Status must not be null")
    private ProductStatus status;

    @NotNull(message = "Category id must not be null")
    @Positive(
            message = "Category id must be greater than 0"
    )
    private Long category_id;
}

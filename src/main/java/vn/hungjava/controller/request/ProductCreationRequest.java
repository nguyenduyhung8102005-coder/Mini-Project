package vn.hungjava.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import vn.hungjava.common.ProductStatus;
import vn.hungjava.model.CategoryEntity;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreationRequest {
    @NotBlank(message = "name must be not blank")
    private String name;
    private String description;
    @NotNull(message = "price must be not null")
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private ProductStatus status;
    @NotNull(message = "category_id must be not null")
    @Positive(message = "category_id must be more than 0")
    private Long category_id;
}

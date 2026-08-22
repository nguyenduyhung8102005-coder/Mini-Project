package vn.hungjava.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.hungjava.common.ProductStatus;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {
    private long id;
    @NotNull(message = "name must be not null")
    private String name;
    private String description;
    @NotNull(message = "price must be not null")
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private ProductStatus status;
    @NotNull(message = "category_id must be not null")
    private long category_id;
}

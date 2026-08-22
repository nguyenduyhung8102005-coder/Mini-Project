package vn.hungjava.controller.request;

import lombok.Getter;
import vn.hungjava.common.ProductStatus;
import vn.hungjava.model.CategoryEntity;

import java.math.BigDecimal;

@Getter
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private ProductStatus status;
}

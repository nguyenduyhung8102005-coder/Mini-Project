package vn.hungjava.controller.request;

import lombok.Getter;
import vn.hungjava.common.ProductStatus;
import vn.hungjava.model.CategoryEntity;

@Getter
public class ProductRequest {
    private String name;
    private String description;
    private float price;
    private Integer stock;
    private String sku;
    private ProductStatus status;
}

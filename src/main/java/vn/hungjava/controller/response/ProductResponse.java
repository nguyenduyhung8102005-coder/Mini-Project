package vn.hungjava.controller.response;

import lombok.*;
import vn.hungjava.common.ProductStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private long id;
    private String name;
    private String description;
    private float price;
    private Integer stock;
    private String sku;
    private ProductStatus status;
}

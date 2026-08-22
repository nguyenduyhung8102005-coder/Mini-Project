package vn.hungjava.controller.response;

import lombok.*;
import vn.hungjava.common.ProductStatus;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private ProductStatus status;
}

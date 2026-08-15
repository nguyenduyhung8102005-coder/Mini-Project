package vn.hungjava.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddCartRequest {
    @Min(value = 1, message = "Product_id must be greater than 0")
    private long productId;

    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;
}

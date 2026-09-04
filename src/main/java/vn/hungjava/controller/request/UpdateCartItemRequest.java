package vn.hungjava.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateCartItemRequest {

    @NotNull(message = "Quantity must be not null")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
}

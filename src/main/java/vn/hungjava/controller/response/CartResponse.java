package vn.hungjava.controller.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long cartId;

    private Long userId;

    private Long totalItems;

    private BigDecimal totalPrice;

    private List<CartItemResponse> items;
}

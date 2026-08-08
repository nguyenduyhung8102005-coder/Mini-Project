package vn.hungjava.controller.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long cartId;

    private Long userId;

    private Integer totalItems;

    private BigDecimal totalPrice;

    private List<CartItemResponse> items;
}

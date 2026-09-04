package vn.hungjava.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hungjava.controller.request.AddCartRequest;
import vn.hungjava.controller.request.UpdateCartItemRequest;
import vn.hungjava.service.CartService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
@Slf4j
@Validated
@Tag(name = "Cart API")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public Map<String, Object> addProduct(
            @Valid @RequestBody AddCartRequest req
    ) {

        long cartId =
                cartService.addProduct(req);

        return Map.of(
                "message", "Product added to cart",
                "cartId", cartId
        );
    }

    @GetMapping
    public Map<String, Object> getMyCart() {
        return Map.of(
                "message", "My cart",
                "cart", cartService.getMyCart()
        );
    }

    @PatchMapping("/items/{cartItemId}")
    public Map<String, Object> updateQuantity(
            @PathVariable @Min(1) Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        cartService.updateQuantity(
                cartItemId,
                request.getQuantity()
        );

        return Map.of(
                "message",
                "Cart item quantity updated"
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    public Map<String, Object> removeProduct(
            @PathVariable @Min(1) Long cartItemId
    ) {
        cartService.removeProduct(cartItemId);

        return Map.of(
                "message",
                "Product removed from cart"
        );
    }

    @DeleteMapping("/items")
    public Map<String, Object> clearCart() {
        cartService.clearCart();

        return Map.of(
                "message",
                "Cart cleared"
        );
    }
}

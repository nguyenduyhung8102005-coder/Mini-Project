package vn.hungjava.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hungjava.controller.request.AddCartRequest;
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
}

package vn.hungjava.service;

import vn.hungjava.controller.request.AddCartRequest;
import vn.hungjava.controller.response.CartResponse;

public interface CartService {
    long addProduct(AddCartRequest req);

    CartResponse getMyCart();

    void updateQuantity(Long cartItemId, Integer quantity);

    void removeProduct(Long cartItemId);

    void clearCart();
}

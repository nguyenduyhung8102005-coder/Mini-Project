package vn.hungjava.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hungjava.common.ProductStatus;
import vn.hungjava.controller.request.AddCartRequest;
import vn.hungjava.controller.response.CartItemResponse;
import vn.hungjava.controller.response.CartResponse;
import vn.hungjava.exception.InvalidDataException;
import vn.hungjava.exception.ResouceNotFoundException;
import vn.hungjava.model.CartEntity;
import vn.hungjava.model.CartItemEntity;
import vn.hungjava.model.ProductEntity;
import vn.hungjava.model.UserEntity;
import vn.hungjava.repository.CartItemRepository;
import vn.hungjava.repository.CartRepository;
import vn.hungjava.repository.ProductRepository;
import vn.hungjava.repository.UserRepository;
import vn.hungjava.service.CartService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private  final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    @Override
    public long addProduct(AddCartRequest req) {
        //1 kiem tra so luong
        if (req.getQuantity() <= 0) {
            throw new InvalidDataException("Quantity must be greater than 0");
        }
        //2 lay user hien tai
        UserEntity user = getCurrentUser();

        //3 tim product
        ProductEntity product =
                productRepository
                        .findById(req.getProductId())
                        .orElseThrow(() ->
                                new InvalidDataException(
                                        "Product not found"
                                )
                        );
        //4 check status
        if(product.getStatus() != ProductStatus.ACTIVE){
            throw new InvalidDataException("Product is not available");
        }

        //5 find or create cart
        CartEntity cart = cartRepository.findByUserId(user.getId()).orElseGet(()-> createCart(user));
        Optional<CartItemEntity> existingItem = findCartItem(cart, product);

        if(existingItem.isPresent()){
            CartItemEntity item = existingItem.get();
            int newQuantity =  item.getQuantity() + req.getQuantity();
            if(newQuantity > product.getStock()){
                throw new InvalidDataException("Insufficient stock");
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            if(req.getQuantity() > product.getStock()){
                throw new InvalidDataException("Insufficient stock");
            }
            CartItemEntity item = new CartItemEntity();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(req.getQuantity());
            item.setPrice(product.getPrice());
            cartItemRepository.save(item);
        }
        return cart.getId();
    }

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new InvalidDataException("User is not authenticated");
        }

        String username =  authentication.getName();
        UserEntity user = userRepository.findByUsername(username);
        if(user == null){
            throw new InvalidDataException("User not found");
        }
        return user;
    }

    public CartEntity createCart(UserEntity user) {
        CartEntity cart =  new CartEntity();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    public Optional<CartItemEntity> findCartItem(CartEntity cart, ProductEntity product) {
        return cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

    }
    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        // 1. Lấy user hiện tại
        UserEntity user = getCurrentUser();

        // 2. Tìm cart
        CartEntity cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new InvalidDataException("Cart not found")
                );

        // 3. Lấy danh sách cart item
        List<CartItemEntity> cartItems = cart.getCartItems();

        // 4. Tạo danh sách response
        List<CartItemResponse> itemResponses = new ArrayList<>();

        // 5. Duyệt từng CartItemEntity
        for (CartItemEntity item : cartItems) {

            CartItemResponse itemResponse = new CartItemResponse();

            itemResponse.setCartItemId(item.getId());
            itemResponse.setProductId(item.getProduct().getId());
            itemResponse.setProductName(item.getProduct().getName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPrice(item.getPrice());

            itemResponses.add(itemResponse);
        }

        long totalItems = cart.getCartItems()
                .stream()
                .mapToLong(CartItemEntity::getQuantity)
                .sum();
        BigDecimal totalPrice = cart.getCartItems()
                .stream()
                .map(item ->
                        item.getPrice()
                                .multiply(
                                        BigDecimal.valueOf(item.getQuantity())
                                )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        // 6. Tạo CartResponse
        CartResponse cartResponse = new CartResponse();

        cartResponse.setCartId(cart.getId());
        cartResponse.setUserId(user.getId());
        cartResponse.setTotalItems(totalItems);
        cartResponse.setTotalPrice(totalPrice);

        // GÁN DANH SÁCH ITEM Ở ĐÂY
        cartResponse.setItems(itemResponses);

        // 7. return
        return cartResponse;
    }

    @Override
    public void updateQuantity(Long cartItemId, Integer quantity) {

    }

    @Override
    public void removeProduct(Long cartItemId) {

    }

    @Override
    public void clearCart() {

    }
}

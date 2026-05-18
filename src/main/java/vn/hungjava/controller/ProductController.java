package vn.hungjava.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hungjava.controller.request.ProductCreationRequest;
import vn.hungjava.controller.request.ProductRequest;
import vn.hungjava.controller.request.ProductUpdateRequest;
import vn.hungjava.controller.response.ProductPageResponse;
import vn.hungjava.controller.response.UserPageResponse;
import vn.hungjava.service.ProductService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
@Slf4j
@Validated
@Tag(name = "API PRODUCT")
public class ProductController {
    private ProductService productService;

    @GetMapping("/{productId}")
    public Map<String, Object> getProductById(@PathVariable @Min(1) long productId){
        log.info("get product by id {}", productId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Get product by id successfully");
        result.put("data", productService.findById(productId));
        return result;
    }

    @PostMapping("/add")
    public Map<String, Object> addProduct(@RequestBody @Valid ProductCreationRequest product){
        log.info("Adding product {}", product);
        productService.save(product);
        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Add product successfully");
        result.put("data", "");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> updateProduct(@RequestBody @Valid ProductUpdateRequest product){
        log.info("updateProduct");
        productService.update(product);
        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Update product successfully");
        result.put("data", "");
        return  result;
    }

    @DeleteMapping("/delete/{productId}")
    public Map<String, Object> deleteProduct(@PathVariable @Min(1) long productId){
        log.info("Delete product by id {}", productId);
        productService.delete(productId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Delete product successfully");
        result.put("data", "");
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> getProductList(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String sort,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size){
        log.info("get product list");
        ProductPageResponse products = productService.findAll(keyword, sort, page, size);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Get product list successfully");
        result.put("data", products);
        return result;
    }

}

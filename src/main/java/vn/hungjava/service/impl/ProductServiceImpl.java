package vn.hungjava.service.impl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.hungjava.common.ProductStatus;
import vn.hungjava.controller.request.ProductCreationRequest;
import vn.hungjava.controller.request.ProductUpdateRequest;
import vn.hungjava.controller.response.ProductPageResponse;
import vn.hungjava.controller.response.ProductResponse;
import vn.hungjava.controller.response.UserPageResponse;
import vn.hungjava.controller.response.UserResponse;
import vn.hungjava.exception.InvalidDataException;
import vn.hungjava.exception.ResouceNotFoundException;
import vn.hungjava.model.CategoryEntity;
import vn.hungjava.model.ProductEntity;
import vn.hungjava.model.UserEntity;
import vn.hungjava.repository.CategoryRepository;
import vn.hungjava.repository.ProductRepository;
import vn.hungjava.service.ProductService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
@Tag(name = "PRODUCT-SERVICE")
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("Find all products");
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if(StringUtils.hasLength(sort)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
                String columName =  matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("esc")){
                    order =  new Sort.Order(Sort.Direction.ASC, columName);
                } else {
                    order =  new Sort.Order(Sort.Direction.DESC, columName);
                }
            }
        }

        int pageNo = 0;
        if(page > 0){
            pageNo = page - 1;
        }
        //phan trang
        Pageable pageAble = PageRequest.of(pageNo, size, Sort.by(order));
        //Tim kiem
        Page<ProductEntity> entityPage = null;
        if(StringUtils.hasLength(keyword)){
            keyword = "%"+ keyword.toLowerCase() + "%";
            entityPage = productRepository.searchByKeyword(keyword, pageAble);
        } else {
            entityPage = productRepository.findAll(pageAble);
        }
        ProductPageResponse response = getProductPageResponse(pageNo, size, entityPage);
        return response;
    }

    @Override
    public ProductResponse findById(long id) {
        ProductEntity product = getById(id);
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .sku(product.getSku())
                .status(product.getStatus())
                .build();
    }

    @Override
    public long save(ProductCreationRequest req) {
        ProductEntity product = productRepository.findByName(req.getName());
        if(product != null) {
            throw new InvalidDataException("Product already exists");
        }
        CategoryEntity category = categoryRepository.findById(req.getCategory_id()).orElseThrow(() -> new ResouceNotFoundException("Category not found"));
        ProductEntity newProduct = new ProductEntity();
        newProduct.setName(req.getName());
        newProduct.setDescription(req.getDescription());
        newProduct.setPrice(req.getPrice());
        newProduct.setStock(req.getStock());
        newProduct.setSku(req.getSku());
        newProduct.setStatus(req.getStatus());
        newProduct.setCategory(category);
        productRepository.save(newProduct);

        return 0;
    }

    @Override
    public void update(ProductUpdateRequest req) {
        ProductEntity  product = getById(req.getId());
        CategoryEntity category =  categoryRepository.findById(req.getCategory_id()).orElseThrow(() -> new ResouceNotFoundException("Category not found"));
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setStock(req.getStock());
        product.setSku(req.getSku());
        product.setStatus(req.getStatus());
        product.setCategory(category);
        productRepository.save(product);

    }

    @Override
    public void delete(long id) {
        ProductEntity product = getById(id);
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    public ProductEntity getById(long id){
        return productRepository.findById(id).orElseThrow(() -> new ResouceNotFoundException("Product not found"));
    }

    private static @NonNull ProductPageResponse getProductPageResponse(int page, int size, Page<ProductEntity> productEntities) {
        log.info("Convert userEntity");
        List<ProductResponse> productList = productEntities.stream().map(entity -> ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .sku(entity.getSku())
                .status(entity.getStatus())
                .build()
        ).toList();

        ProductPageResponse response = new ProductPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(productEntities.getTotalElements());
        response.setTotalPages(productEntities.getTotalPages());
        response.setProducts(productList);
        return response;
    }
}

package vn.hungjava.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.hungjava.common.CategoryStatus;
import vn.hungjava.common.UserStatus;
import vn.hungjava.controller.request.CategoryCreationResquest;
import vn.hungjava.controller.request.CategoryUpdateRequest;
import vn.hungjava.controller.response.CategoryPageResponse;
import vn.hungjava.controller.response.CategoryResponse;
import vn.hungjava.exception.InvalidDataException;
import vn.hungjava.model.CategoryEntity;
import vn.hungjava.model.ProductEntity;
import vn.hungjava.model.UserEntity;
import vn.hungjava.repository.CategoryRepository;
import vn.hungjava.repository.ProductRepository;
import vn.hungjava.service.CategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    @Override
    public CategoryResponse findById(long id) {
        CategoryEntity category = getById(id);
        return CategoryResponse.builder()
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    public CategoryResponse findByName(String name) {
        return null;
    }

    @Override
    public CategoryPageResponse findAll(String keyword, String sort, int page, int size) {
        //sử dụng common của spring boot để check keyword, vừa check null vừa check blank
        log.info("findAll");
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if(StringUtils.hasLength(sort)) {
            Pattern patten =  Pattern.compile("(\\w+?)(:)(.*)"); // tencot:asc/desc
            Matcher matcher = patten.matcher(sort);
            if(matcher.find()) {
                String columnName =  matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    order =  new Sort.Order(Sort.Direction.ASC, columnName);
                } else {
                    order =  new Sort.Order(Sort.Direction.DESC, columnName);
                }
            }
        }
        //Xu ly truong hop FE muon bat dau voi page = 1
        int pageNo = 0;
        if(page > 0) {
            pageNo = page - 1;
        }
        //Phan trang
        Pageable pageable =  PageRequest.of(pageNo, size, Sort.by(order));
        //tim kiem
        Page<CategoryEntity> entityPage = null;
        if(StringUtils.hasLength(keyword)) {
            //goi search method
            keyword = "%" + keyword.toLowerCase() + "%";
            entityPage = categoryRepository.findByKeyWord(keyword, pageable);
        } else {
            entityPage = categoryRepository.findAll(pageable);
        }

        CategoryPageResponse response = getCategoryPageResponse(page, size, entityPage);
        return response;
    }

    @Override
    @Transactional
    public long save(CategoryCreationResquest req) {
        log.info("Saving category {}", req);

        CategoryEntity categoryByName = categoryRepository.findByName(req.getName());
        if(categoryByName != null){
            throw new InvalidDataException("Category already exists");
        }

        CategoryEntity category = new CategoryEntity();
        category.setName(req.getName());
        category.setDescription(req.getDescription());
        categoryRepository.save(category);

        if(category.getId() != null){
            List<ProductEntity> products = new ArrayList<>();
            req.getProducts().forEach(product -> {
                ProductEntity productEntity = new ProductEntity();
                productEntity.setName(product.getName());
                productEntity.setDescription(product.getDescription());
                productEntity.setPrice(product.getPrice());
                productEntity.setStock(product.getStock());
                productEntity.setSku(product.getSku());
                productEntity.setStatus(product.getStatus());
                productEntity.setCategory(category);
                products.add(productEntity);

            });
            log.info("Saving category {}", category);
            productRepository.saveAll(products);
        }
        return category.getId();
    }

    @Override
    public void delete(long id) {
        CategoryEntity category = getById(id);
        category.setStatus(CategoryStatus.INACTIVE);
        categoryRepository.save(category);
    }

    @Override
    public void update(CategoryUpdateRequest category) {
        CategoryEntity categoryById = getById(category.getId());
        categoryById.setName(category.getName());
        categoryById.setDescription(category.getDescription());
        categoryRepository.save(categoryById);
    }

    public CategoryEntity getById(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new InvalidDataException("Category not found"));
    }

    private static @NonNull CategoryPageResponse getCategoryPageResponse(int page, int size, Page<CategoryEntity> categoryEntities) {
        log.info("Convert userEntity");
        List<CategoryResponse> categoryList = categoryEntities.stream().map(entity -> CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build()
        ).toList();

        CategoryPageResponse response = new CategoryPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(categoryEntities.getTotalElements());
        response.setTotalPages(categoryEntities.getTotalPages());
        response.setCategories(categoryList);
        return response;
    }
}

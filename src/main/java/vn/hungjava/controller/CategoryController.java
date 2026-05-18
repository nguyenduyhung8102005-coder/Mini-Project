package vn.hungjava.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hungjava.controller.request.CategoryCreationResquest;
import vn.hungjava.controller.request.CategoryUpdateRequest;
import vn.hungjava.controller.response.CategoryPageResponse;
import vn.hungjava.controller.response.CategoryResponse;
import vn.hungjava.controller.response.UserPageResponse;
import vn.hungjava.service.CategoryService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
@Slf4j
@Validated
@Tag(name = "Category API")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/list")
    public Map<String, Object> getListCategory(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) String sort,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size){
        log.info("Get list category");
        CategoryPageResponse categories = categoryService.findAll(keyword, sort, page, size);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Get list users");
        result.put("data", categories);
        return  result;
    }

    @GetMapping("/{categoryId}")
    public Map<String, Object> getCategoryById(@PathVariable @Min(1) long categoryId){
        log.info("Get category by id: {}", categoryId);
        CategoryResponse category = categoryService.findById(categoryId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Get category by id: " + categoryId);
        result.put("data", category);
        return  result;
    }

    @PostMapping("/add")
    public Map<String, Object> createCategory(@RequestBody @Valid CategoryCreationResquest category){
        log.info("Create Category");
        categoryService.save(category);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Category has been successfully created");
        result.put("data", "");
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> updateCategory(@RequestBody @Valid CategoryUpdateRequest category){
        categoryService.update(category);
        Map<String,  Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Category has been successfully updated");
        result.put("data", "");
        return  result;
    }
    @DeleteMapping("/delete/{categoryId}")
    public Map<String, Object> deleteCategory(@PathVariable @Min(1) long categoryId){
        log.info("Delete Category");
        categoryService.delete(categoryId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Category has been successfully deleted");
        result.put("data", "");
        return result;
    }

}

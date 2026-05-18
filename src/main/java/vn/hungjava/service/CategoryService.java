package vn.hungjava.service;

import org.springframework.data.domain.Page;
import vn.hungjava.controller.request.CategoryCreationResquest;
import vn.hungjava.controller.request.CategoryUpdateRequest;
import vn.hungjava.controller.response.CategoryPageResponse;
import vn.hungjava.controller.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse findById(long id);
    CategoryResponse findByName(String name);
    CategoryPageResponse findAll(String keyword, String sort, int  page, int size);
    long save(CategoryCreationResquest req);
    void delete(long id);
    void update(CategoryUpdateRequest category);
}

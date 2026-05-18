package vn.hungjava.service;

import vn.hungjava.controller.request.ProductCreationRequest;
import vn.hungjava.controller.request.ProductUpdateRequest;
import vn.hungjava.controller.response.CategoryResponse;
import vn.hungjava.controller.response.ProductPageResponse;
import vn.hungjava.controller.response.ProductResponse;
import vn.hungjava.controller.response.UserPageResponse;

public interface ProductService {
    ProductPageResponse findAll(String keyword, String sort, int page, int size);
    ProductResponse findById(long id);
    long save(ProductCreationRequest req);
    void update(ProductUpdateRequest req);
    void delete(long id);
}

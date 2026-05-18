package vn.hungjava.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryPageResponse extends PageResponseAbtract {
    private List<CategoryResponse> categories;
}

package vn.hungjava.controller.response;

import lombok.*;
import vn.hungjava.controller.request.ProductRequest;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private long id;
    private String name;
    private String description;
}

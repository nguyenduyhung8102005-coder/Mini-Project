package vn.hungjava.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class CategoryCreationResquest {
    @NotNull(message = "name must be not null")
    private String name;
    private String description;
    private List<ProductRequest> products;
}

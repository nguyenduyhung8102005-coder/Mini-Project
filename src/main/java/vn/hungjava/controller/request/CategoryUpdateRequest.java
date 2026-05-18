package vn.hungjava.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CategoryUpdateRequest {
    private long id;
    @NotNull(message = "name must be not null")
    private String name;
    private String description;
}

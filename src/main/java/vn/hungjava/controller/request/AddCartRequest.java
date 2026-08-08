package vn.hungjava.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddCartRequest {
    private long productId;
    private long quantity;
}

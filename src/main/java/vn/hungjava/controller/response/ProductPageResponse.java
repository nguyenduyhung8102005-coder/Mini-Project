package vn.hungjava.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProductPageResponse extends PageResponseAbtract{
    private List<ProductResponse> products;
}

package vn.hungjava.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponseAbtract {
    public int pageNumber;
    public int pageSize;
    public long totalPages;
    public long totalElements;
}

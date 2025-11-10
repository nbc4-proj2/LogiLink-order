package order_service.order.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageableUtils {
    private static final List<Integer> ALLOWED_SIZES = List.of(10,30,50);
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    public static org.springframework.data.domain.Pageable enforce(org.springframework.data.domain.Pageable pageable){
        return enforce(pageable, DEFAULT_SORT);
    }

    public static org.springframework.data.domain.Pageable enforce (org.springframework.data.domain.Pageable pageable, Sort defaultSort){
        int page = Math.max(0, pageable.getPageNumber());
        int size = ALLOWED_SIZES.contains(pageable.getPageSize()) ? pageable.getPageSize():10;
        Sort sort = pageable.getSort().isUnsorted() ? defaultSort : pageable.getSort();

        return PageRequest.of(page, size, sort);
    }

    private PageableUtils() {}
}

package com.joinbe.common.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PaginationUtil {

    public static <T> Page<T> toMpPage(Pageable pageable) {
        return new Page<T>(pageable.getPageNumber(), pageable.getPageSize());
    }

    public static <T> org.springframework.data.domain.Page<T> toSpringDataPage(Page<T> page, Pageable pageable) {
        PageImpl<T> springPage = new PageImpl<T>(page.getRecords(), pageable, page.getTotal());
        return springPage;
    }


}

package com.carlosvc.repaso_springboot.utils;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<A>(
        List<A> content,
        int totalPages,
        long totalElements,
        int pageSize,
        int pageNumber,
        int totalPageElements,
        boolean empty,
        boolean first,
        boolean last,
        String sortBy,
        String direction
) {

    public static <A> PageResponse<A> of(Page<A> page, String sortBy, String direction) {
        return  new PageResponse<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.getNumberOfElements(),
                page.isEmpty(),
                page.isFirst(),
                page.isLast(),
                sortBy,
                direction
        );
    }

}

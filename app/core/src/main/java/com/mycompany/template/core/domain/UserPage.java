package com.mycompany.template.core.domain;

import java.util.List;

public record UserPage(
        List<User> content,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) {
}

package com.huy.library.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.huy.library.dto.request.CategoryCreateRequest;
import com.huy.library.dto.request.CategoryUpdateRequest;
import com.huy.library.dto.response.CategoryResponse;
import com.huy.library.dto.response.CategorySummaryResponse;
import com.huy.library.entity.Category;

@Component
public class CategoryMapper {
    public Category toEntity(CategoryCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public void updateEntity(Category category, CategoryUpdateRequest request) {
        category.setName(request.name());
        category.setDescription(request.description());
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt());
    }

    public CategorySummaryResponse tpSummary(Category category) {
        if (category == null) {
            return null;
        }

        return new CategorySummaryResponse(
                category.getId(),
                category.getName());
    }

    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .toList();
    }
}

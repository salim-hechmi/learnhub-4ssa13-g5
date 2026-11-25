package com.learnhub.course.service;

import com.learnhub.course.dto.*;
import java.util.List;

public interface CategoryService {
    List<CategoryResponseDTO> getAllCategories();
    CategoryResponseDTO getCategoryById(Integer id);
    CategoryResponseDTO createCategory(CategoryRequestDTO request);
    CategoryResponseDTO updateCategory(Integer id, CategoryRequestDTO request);
    void deleteCategory(Integer id);
}

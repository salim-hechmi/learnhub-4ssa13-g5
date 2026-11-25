package com.learnhub.course.service.impl;

import com.learnhub.course.dto.CategoryRequestDTO;
import com.learnhub.course.dto.CategoryResponseDTO;
import com.learnhub.course.exception.DuplicateResourceException;
import com.learnhub.course.exception.ResourceNotFoundException;
import com.learnhub.course.model.Category;
import com.learnhub.course.repository.CategoryRepository;
import com.learnhub.course.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id: " + id));
        return mapToDTO(category);
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Une catégorie avec le nom '" + request.getName() + "' existe déjà");
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Category saved = categoryRepository.save(category);
        log.info("Catégorie créée : id={}, name={}", saved.getId(), saved.getName());
        return mapToDTO(saved);
    }

    @Override
    public CategoryResponseDTO updateCategory(Integer id, CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'id: " + id));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return mapToDTO(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Catégorie introuvable avec l'id: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Catégorie supprimée : id={}", id);
    }

    private CategoryResponseDTO mapToDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .coursesCount(category.getCourses() != null ? category.getCourses().size() : 0)
                .build();
    }
}

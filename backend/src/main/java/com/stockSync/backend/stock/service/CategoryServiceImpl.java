package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.dto.CategoryRequest;
import com.stockSync.backend.stock.dto.CategoryResponse;
import com.stockSync.backend.stock.mapper.CategoryMapper;
import com.stockSync.backend.stock.model.Category;
import com.stockSync.backend.stock.repository.CategoryRepository;
import com.stockSync.backend.common.exception.ResourceNotFoundException;
import com.stockSync.backend.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends BaseService implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toResponseList(categoryRepository.findByUserId(getTenantId()));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        if (!category.getUser().getId().equals(getTenantId())) {
            throw new ResourceNotFoundException("Categoria no encontrada");
        }
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        // Verificamos si existe en ESTE entorno
        if (categoryRepository.existsByNameAndUserId(request.getName(), getTenantId())) {
            throw new ConflictException("La categoria '" + request.getName() + "' ya existe en tu organización");
        }
        Category category = categoryMapper.toEntity(request);
        category.setUser(getTenantUser());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        if (!category.getUser().getId().equals(getTenantId())) {
            throw new ResourceNotFoundException("Categoria no encontrada");
        }

        categoryMapper.updateEntityFromRequest(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        if (!category.getUser().getId().equals(getTenantId())) {
            throw new ResourceNotFoundException("Categoria no encontrada");
        }

        if (!category.getProducts().isEmpty()) {
            throw new ConflictException("No se puede eliminar: La categoria tiene productos.");
        }

        categoryRepository.delete(category);
    }
}
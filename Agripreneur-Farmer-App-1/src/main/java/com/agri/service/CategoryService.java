package com.agri.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agri.exception.ResourceNotFoundException;
import com.agri.model.Category;
import com.agri.repository.CategoryRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category addCategory(Category category) {
        Long nextId = getNextAvailableId();
        category.setId(nextId);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for this id: " + id));
        category.setName(categoryDetails.getName());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for this id: " + id));
        categoryRepository.delete(category);
    }

    private Long getNextAvailableId() {
        List<Long> ids = categoryRepository.findAllIds();
        Long nextId = 1L;
        for (Long id : ids) {
            if (!id.equals(nextId)) {
                break;
            }
            nextId++;
        }
        return nextId;
    }
}

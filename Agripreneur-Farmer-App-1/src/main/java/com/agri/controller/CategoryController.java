package com.agri.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agri.exception.ResourceNotFoundException;
import com.agri.model.Category;
import com.agri.service.CategoryService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<List<Category>> getAllCategories() {
    	List<Category> categories= categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryByI(@PathVariable(value = "id")Long id) {
    	Category category=categoryService.getCategoryById(id).orElseThrow(()-> new ResourceNotFoundException("Category not found for this id:"+id));
    	return ResponseEntity.ok(category);
    }
    
    //add new category
    @PostMapping("/addCategory")
    public ResponseEntity<Map<String, Object>> addCategory(@Valid @RequestBody Category category) {
        Category createdCategory = categoryService.addCategory(category);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Category was successfully created.");
        response.put("category", createdCategory);
        return ResponseEntity.ok(response);
    }

    //update an Existing category
    @PutMapping("updateCategory/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails)
    {
        Category updatedcategory= categoryService.updateCategory(id, categoryDetails);
        return ResponseEntity.ok(updatedcategory);
         
    }
    
 // Delete a category
    @DeleteMapping("deleteCategory/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable(value = "id") Long id) {
        categoryService.deleteCategory(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Category with ID " + id + " was successfully deleted.");
        return ResponseEntity.ok(response);
    }
    }


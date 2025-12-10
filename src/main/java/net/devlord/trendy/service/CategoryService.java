package net.devlord.trendy.service;

import net.devlord.trendy.model.entity.Category;
import net.devlord.trendy.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAllOrderBySortOrder();
    }
    
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    @Transactional
    public Category createCategory(Category category) {
        log.info("Creating new category: {}", category.getName());
        return categoryRepository.save(category);
    }
    
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        category.setName(categoryDetails.getName());
        category.setDisplayName(categoryDetails.getDisplayName());
        category.setDescription(categoryDetails.getDescription());
        category.setIcon(categoryDetails.getIcon());
        category.setSortOrder(categoryDetails.getSortOrder());
        
        log.info("Updated category: {}", id);
        return categoryRepository.save(category);
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        log.info("Deleted category: {}", id);
    }
    
    @Transactional
    public Category getOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
            .orElseGet(() -> {
                Category category = new Category();
                category.setName(name);
                category.setDisplayName(name);
                return categoryRepository.save(category);
            });
    }
}

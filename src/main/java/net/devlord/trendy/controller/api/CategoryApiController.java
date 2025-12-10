package net.devlord.trendy.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.devlord.trendy.model.dto.CategoryDTO;
import net.devlord.trendy.model.entity.Category;
import net.devlord.trendy.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryApiController {
    
    private final CategoryService categoryService;
    
    @Operation(
        summary = "Get all categories",
        description = "Retrieve all categories ordered by sort order",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved categories"
            )
        }
    )
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }
    
    @Operation(
        summary = "Get category by ID",
        description = "Retrieve a specific category by its ID",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved category"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Category not found"
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id) {
        return categoryService.getCategoryById(id)
            .map(category -> ResponseEntity.ok(convertToDTO(category)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "Get category by name",
        description = "Retrieve a specific category by its name",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved category"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Category not found"
            )
        }
    )
    @GetMapping("/name/{name}")
    public ResponseEntity<CategoryDTO> getCategoryByName(
            @Parameter(description = "Category name", required = true)
            @PathVariable String name) {
        return categoryService.getCategoryByName(name)
            .map(category -> ResponseEntity.ok(convertToDTO(category)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDisplayName(category.getDisplayName());
        dto.setDescription(category.getDescription());
        dto.setIcon(category.getIcon());
        dto.setSortOrder(category.getSortOrder());
        dto.setTrendCount(category.getTrends() != null ? category.getTrends().size() : 0);
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
    }
}

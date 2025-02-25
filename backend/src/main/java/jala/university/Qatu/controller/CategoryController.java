package jala.university.Qatu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jala.university.Qatu.domain.product.Category;
import jala.university.Qatu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Category Controller", description = "Endpoints for managing categories. These endpoints are accessible everyone, except the edition mode for administrators and sellers")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Get All Categories", description = "Retrieve all categories in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get a Category", description = "Retrieve only one category in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable @Parameter(description = "UUID of the category") UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Create New Category", description = "Create a new category in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody @Parameter(description = "Details of the category to be created") Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(201).body(createdCategory);
    }

    @Operation(summary = "Update Existing Category", description = "Update an existing category in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable @Parameter(description = "UUID of the category to be updated") UUID id,
                                                   @RequestBody @Parameter(description = "Details of the category to be updated") Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete Category", description = "Delete a category from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Parameter(description = "UUID of the category to be deleted") UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
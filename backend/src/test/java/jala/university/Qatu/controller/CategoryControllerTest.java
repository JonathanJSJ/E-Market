package jala.university.Qatu.controller;

import jala.university.Qatu.domain.product.Category;
import jala.university.Qatu.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCategories_Success() {
        // Arrange
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(UUID.randomUUID(), "Electronics", null));
        categories.add(new Category(UUID.randomUUID(), "Books", null));

        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act
        ResponseEntity<List<Category>> response = categoryController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void getCategoryById_Success() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = new Category(categoryId, "Electronics", null);

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        // Act
        ResponseEntity<Category> response = categoryController.getCategoryById(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
        verify(categoryService, times(1)).getCategoryById(categoryId);
    }

    @Test
    void createCategory_Success() {
        // Arrange
        Category category = new Category(null, "Electronics", null);
        Category createdCategory = new Category(UUID.randomUUID(), "Electronics", null);

        when(categoryService.createCategory(any(Category.class))).thenReturn(createdCategory);

        // Act
        ResponseEntity<Category> response = categoryController.createCategory(category);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdCategory, response.getBody());
        verify(categoryService, times(1)).createCategory(category);
    }

    @Test
    void updateCategory_Success() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = new Category(null, "Updated Electronics", null);
        Category updatedCategory = new Category(categoryId, "Updated Electronics", null);

        when(categoryService.updateCategory(eq(categoryId), any(Category.class))).thenReturn(updatedCategory);

        // Act
        ResponseEntity<Category> response = categoryController.updateCategory(categoryId, category);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
        verify(categoryService, times(1)).updateCategory(categoryId, category);
    }

    @Test
    void deleteCategory_Success() {
        // Arrange
        UUID categoryId = UUID.randomUUID();

        // Act
        ResponseEntity<Void> response = categoryController.deleteCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService, times(1)).deleteCategory(categoryId);
    }
}

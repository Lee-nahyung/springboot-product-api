package com.example.productapi.service;

import com.example.productapi.domain.entity.Category;
import com.example.productapi.domain.repository.CategoryRepository;
import com.example.productapi.dto.CategoryDto;
import com.example.productapi.mapper.CategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setup() {
        category = Category.builder().id(1L).name("Book").build();
        categoryDto = CategoryDto.builder().id(1L).name("Book").build();
    }

    @Test
    @DisplayName("카테고리 생성")
    void createCategory() {
        Category toEntity = Category.builder().name("Book").build();
        Category saved = Category.builder().id(1L).name("Book").build();

        when(categoryMapper.toCategory(any(CategoryDto.class))).thenReturn(toEntity);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);
        when(categoryMapper.toCategoryDto(any(Category.class))).thenReturn(categoryDto);

        CategoryDto result = categoryService.createCategory(CategoryDto.builder().name("Book").build());

        assertNotNull(result);
        assertEquals("Book", result.getName());
    }

    @Test
    @DisplayName("전체 카테고리 조회")
    void getAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toCategoryDto(any(Category.class))).thenReturn(categoryDto);

        List<CategoryDto> results = categoryService.getAllCategories();

        assertEquals(1, results.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("카테고리 ID로 조회 - 성공")
    void getCategoryById_success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Book", result.getName());
    }

    @Test
    @DisplayName("카테고리 ID로 조회 - 실패")
    void getCategoryById_fail() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void updateCategory_success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryMapper.toCategory(any(CategoryDto.class))).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryDto(category)).thenReturn(categoryDto);

        CategoryDto updated = categoryService.updateCategory(1L, categoryDto);

        assertEquals("Book", updated.getName());
    }

    @Test
    @DisplayName("카테고리 수정 - 실패")
    void updateCategory_fail() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> categoryService.updateCategory(1L, categoryDto));
    }

    @Test
    @DisplayName("카테고리 삭제 - 성공")
    void deleteCategory_success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("카테고리 삭제 - 실패")
    void deleteCategory_fail() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> categoryService.deleteCategory(1L));
    }
}


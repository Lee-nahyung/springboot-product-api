package com.example.productapi.controller;

import com.example.productapi.dto.CategoryDto;
import com.example.productapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 카테고리(Category) 관련 API를 처리하는 컨트롤러
 * <p>
 * 이 컨트롤러는 상품 카테고리의 생성, 조회, 수정, 삭제 등 카테고리와 관련된 모든 작업을 담당합니다.
 * 카테고리 목록 조회 및 관리 기능을 제공합니다.
 * </p>
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 새로운 카테고리를 생성합니다.
     * 
     * @param categoryDto 카테고리 생성에 필요한 정보가 담긴 DTO 객체
     * @return 생성된 카테고리 정보와 HTTP 201 Created 응답
     */
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * 모든 카테고리 목록을 조회합니다.
     * 
     * @return 전체 카테고리 목록과 HTTP 200 OK 응답
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * ID로 특정 카테고리를 조회합니다.
     * 
     * @param id 조회할 카테고리의 ID
     * @return 카테고리 정보와 HTTP 200 OK 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        CategoryDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * 카테고리 정보를 수정합니다.
     * 
     * @param id 수정할 카테고리의 ID
     * @param categoryDto 수정할 내용이 담긴 DTO 객체
     * @return 수정된 카테고리 정보와 HTTP 200 OK 응답
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDto categoryDto) {
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * 특정 카테고리를 삭제합니다.
     * 
     * @param id 삭제할 카테고리의 ID
     * @return HTTP 204 No Content 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

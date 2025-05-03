package com.example.productapi.service;

import com.example.productapi.domain.entity.Category;
import com.example.productapi.domain.repository.CategoryRepository;
import com.example.productapi.dto.CategoryDto;
import com.example.productapi.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 카테고리(Category) 관련 비즈니스 로직을 처리하는 서비스
 * <p>
 * 이 서비스는 상품 카테고리의 생성, 조회, 수정, 삭제와 관련된 비즈니스 로직을 구현합니다.
 * CategoryRepository를 통해 데이터베이스와 상호작용하며,
 * CategoryMapper를 사용하여 엔티티와 DTO 간의 변환을 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * 새로운 카테고리를 생성합니다.
     * <p>
     * 입력받은 CategoryDto를 바탕으로 새로운 카테고리를 데이터베이스에 저장합니다.
     * </p>
     * 
     * @param categoryDto 생성할 카테고리 정보가 담긴 DTO
     * @return 생성된 카테고리 정보(ID 포함)
     */
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        categoryDto.setId(null); // 새 카테고리 생성 시 ID 필드를 null로 설정
        
        Category category = categoryMapper.toCategory(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(savedCategory);
    }

    /**
     * 모든 카테고리 목록을 조회합니다.
     * <p>
     * 데이터베이스에 저장된 모든 카테고리 정보를 가져와 DTO 리스트로 반환합니다.
     * </p>
     * 
     * @return 전체 카테고리 목록
     */
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    /**
     * ID를 기준으로 특정 카테고리를 조회합니다.
     * <p>
     * 입력받은 ID에 해당하는 카테고리를 찾아 DTO로 반환합니다.
     * 해당 ID의 카테고리가 없는 경우 예외가 발생합니다.
     * </p>
     * 
     * @param id 조회할 카테고리의 ID
     * @return 조회된 카테고리 정보
     * @throws ResponseStatusException 카테고리를 찾을 수 없는 경우 (404 Not Found)
     */
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id: " + id));
        return categoryMapper.toCategoryDto(category);
    }

    /**
     * 카테고리 정보를 수정합니다.
     * <p>
     * 입력받은 ID의 카테고리를 찾아 DTO의 정보로 업데이트합니다.
     * 카테고리 이름, 설명 등의 정보가 변경될 수 있습니다.
     * </p>
     * 
     * @param id 수정할 카테고리의 ID
     * @param categoryDto 수정할 내용이 담긴 DTO
     * @return 수정된 카테고리 정보
     * @throws ResponseStatusException 카테고리를 찾을 수 없는 경우 (404 Not Found)
     */
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        // 기존 카테고리가 존재하는지 확인
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Category not found with id: " + id);
        }
        
        // ID 값 설정하여 업데이트
        categoryDto.setId(id);
        Category category = categoryMapper.toCategory(categoryDto);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    /**
     * 특정 카테고리를 삭제합니다.
     * <p>
     * 입력받은 ID의 카테고리를 데이터베이스에서 제거합니다.
     * 삭제 전 카테고리가 존재하는지 확인하여 없는 경우 예외가 발생합니다.
     * </p>
     * 
     * @param id 삭제할 카테고리의 ID
     * @throws ResponseStatusException 카테고리를 찾을 수 없는 경우 (404 Not Found)
     */
    @Transactional
    public void deleteCategory(Long id) {
        // 기존 카테고리가 존재하는지 확인
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}

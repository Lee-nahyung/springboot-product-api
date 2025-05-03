package com.example.productapi.service;

import com.example.productapi.domain.entity.Category;
import com.example.productapi.domain.entity.Product;
import com.example.productapi.domain.repository.CategoryRepository;
import com.example.productapi.domain.repository.ProductRepository;
import com.example.productapi.dto.ProductDto;
import com.example.productapi.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 상품(Product) 관련 비즈니스 로직을 처리하는 서비스
 * <p>
 * 이 서비스는 상품의 생성, 조회, 수정, 삭제 및 재고 관리와 관련된 비즈니스 로직을 구현합니다.
 * ProductRepository, CategoryRepository를 통해 데이터베이스와 상호작용하며,
 * ProductMapper를 사용하여 엔티티와 DTO 간의 변환을 처리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    /**
     * 새로운 상품을 생성합니다.
     * <p>
     * 입력받은 ProductDto를 바탕으로 새로운 상품을 데이터베이스에 저장합니다.
     * 카테고리 정보가 있는 경우 해당 카테고리와 연결됩니다.
     * </p>
     * 
     * @param productDto 생성할 상품 정보가 담긴 DTO
     * @return 생성된 상품 정보(ID 포함)
     * @throws ResponseStatusException 카테고리를 찾을 수 없는 경우 (404 Not Found)
     */
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        productDto.setId(null); // 새 상품 생성 시 ID 필드를 null로 설정
        
        // 카테고리 검증
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id: " + productDto.getCategoryId()));
        
        Product product = productMapper.toProduct(productDto);
        product.setCategory(category);
        
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDto(savedProduct);
    }

    /**
     * 모든 상품 목록을 조회합니다.
     * <p>
     * 데이터베이스에 저장된 모든 상품 정보를 가져와 DTO 리스트로 반환합니다.
     * </p>
     * 
     * @return 전체 상품 목록
     */
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductDto)
                .toList();
    }

    /**
     * ID를 기준으로 특정 상품을 조회합니다.
     * <p>
     * 입력받은 ID에 해당하는 상품을 찾아 DTO로 반환합니다.
     * 해당 ID의 상품이 없는 경우 예외가 발생합니다.
     * </p>
     * 
     * @param id 조회할 상품의 ID
     * @return 조회된 상품 정보
     * @throws ResponseStatusException 상품을 찾을 수 없는 경우 (404 Not Found)
     */
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id: " + id));
        return productMapper.toProductDto(product);
    }

    /**
     * 상품 정보를 수정합니다.
     * <p>
     * 입력받은 ID의 상품을 찾아 DTO의 정보로 업데이트합니다.
     * 상품 이름, 가격, 설명, 카테고리 등의 정보가 변경될 수 있습니다.
     * </p>
     * 
     * @param id 수정할 상품의 ID
     * @param productDto 수정할 내용이 담긴 DTO
     * @return 수정된 상품 정보
     * @throws ResponseStatusException 상품이나 카테고리를 찾을 수 없는 경우 (404 Not Found)
     */
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        // 기존 상품이 존재하는지 확인
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found with id: " + id);
        }

        // 카테고리 검증
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id: " + productDto.getCategoryId()));
        
        // ID 값 설정하여 업데이트
        productDto.setId(id);
        Product product = productMapper.toProduct(productDto);
        product.setCategory(category);
        
        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductDto(updatedProduct);
    }

    /**
     * 상품의 재고 수량을 업데이트합니다.
     * <p>
     * 입력받은 ID의 상품을 찾아 재고 수량을 변경합니다.
     * 주문 처리, 입고 등의 작업 시 이 메서드를 통해 재고가 관리됩니다.
     * </p>
     * 
     * @param id 재고를 변경할 상품의 ID
     * @param stockQuantity 변경할 재고 수량
     * @return 업데이트된 상품 정보
     * @throws ResponseStatusException 상품을 찾을 수 없는 경우 (404 Not Found) 또는 재고 수량이 음수인 경우 (400 Bad Request)
     */
    @Transactional
    public ProductDto updateProductStock(Long id, int stockQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id: " + id));
        
        if (stockQuantity < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Stock quantity cannot be negative");
        }
        
        product.setStock(stockQuantity);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductDto(updatedProduct);
    }

    /**
     * 특정 상품을 삭제합니다.
     * <p>
     * 입력받은 ID의 상품을 데이터베이스에서 제거합니다.
     * 삭제 전 상품이 존재하는지 확인하여 없는 경우 예외가 발생합니다.
     * </p>
     * 
     * @param id 삭제할 상품의 ID
     * @throws ResponseStatusException 상품을 찾을 수 없는 경우 (404 Not Found)
     */
    @Transactional
    public void deleteUser(Long id) {
        // 기존 상품이 존재하는지 확인
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}

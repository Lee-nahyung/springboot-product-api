package com.example.productapi.service;

import com.example.productapi.domain.entity.Category;
import com.example.productapi.domain.entity.Product;
import com.example.productapi.domain.repository.CategoryRepository;
import com.example.productapi.domain.repository.ProductRepository;
import com.example.productapi.dto.ProductDto;
import com.example.productapi.mapper.ProductMapper;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Category").build();
        product = Product.builder().id(1L).name("Product").price(1000).stock(10).category(category).build();
        productDto = ProductDto.builder().id(1L).name("Product").price(1000).stock(10).categoryId(1L).categoryName("Category").build();
    }

    @Test
    @DisplayName("상품 저장 성공")
    void createProduct_success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.toProduct(any())).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toProductDto(any())).thenReturn(productDto);

        ProductDto result = productService.createProduct(productDto);

        assertEquals("Product", result.getName());
    }

    @Test
    @DisplayName("상품 저장 실패 - 존재하지 않는 카테고리")
    void createProduct_fail_categoryNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.createProduct(productDto));
    }

    @Test
    @DisplayName("상품 전체 조회")
    void getAllProducts_success() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toProductDto(any())).thenReturn(productDto);

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("상품 단건 조회 성공")
    void getProductById_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toProductDto(product)).thenReturn(productDto);

        ProductDto result = productService.getProductById(1L);

        assertEquals("Product", result.getName());
    }

    @Test
    @DisplayName("상품 단건 조회 실패 - 존재하지 않음")
    void getProductById_fail() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.getProductById(1L));
    }

    @Test
    @DisplayName("상품 수정 성공")
    void updateProduct_success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.toProduct(any())).thenReturn(product);
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toProductDto(any())).thenReturn(productDto);

        ProductDto result = productService.updateProduct(1L, productDto);

        assertEquals("Product", result.getName());
    }

    @Test
    @DisplayName("상품 수정 실패 - 상품 없음")
    void updateProduct_fail_notFound() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> productService.updateProduct(1L, productDto));
    }

    @Test
    @DisplayName("상품 재고 수정 성공")
    void updateStock_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toProductDto(any())).thenReturn(productDto);

        ProductDto result = productService.updateProductStock(1L, 20);

        assertEquals(20, product.getStock());
        assertEquals("Product", result.getName());
    }

    @Test
    @DisplayName("상품 재고 수정 실패 - 음수")
    void updateStock_fail_negative() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(ResponseStatusException.class, () -> productService.updateProductStock(1L, -10));
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteUser_success() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteUser(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("상품 삭제 실패 - 존재하지 않음")
    void deleteUser_fail() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> productService.deleteUser(1L));
    }
}

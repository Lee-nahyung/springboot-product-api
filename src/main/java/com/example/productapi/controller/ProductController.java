package com.example.productapi.controller;

import com.example.productapi.dto.ProductDto;
import com.example.productapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 상품(Product) 관련 API를 처리하는 컨트롤러
 * <p>
 * 이 컨트롤러는 상품의 생성, 조회, 수정, 삭제 등 상품과 관련된 모든 작업을 담당합니다.
 * 상품 목록 조회 및 상품 재고 관리 기능을 제공합니다.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 모든 상품 목록을 조회합니다.
     * 
     * @return 전체 상품 목록과 HTTP 200 OK 응답
     */
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 새로운 상품을 생성합니다.
     * 
     * @param productDto 상품 생성에 필요한 정보가 담긴 DTO 객체
     * @return 생성된 상품 정보와 HTTP 201 Created 응답
     */
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        ProductDto createdProduct = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    /**
     * ID로 특정 상품을 조회합니다.
     * 
     * @param id 조회할 상품의 ID
     * @return 상품 정보와 HTTP 200 OK 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    /**
     * 상품 정보를 수정합니다.
     * 
     * @param id 수정할 상품의 ID
     * @param productDto 수정할 내용이 담긴 DTO 객체
     * @return 수정된 상품 정보와 HTTP 200 OK 응답
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id, 
            @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }
    
    /**
     * 상품의 재고 수량을 업데이트합니다.
     * 
     * @param id 재고를 변경할 상품의 ID
     * @param quantity 변경할 재고 수량
     * @return 업데이트된 상품 정보와 HTTP 200 OK 응답
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDto> updateProductStock(
            @PathVariable Long id, 
            @RequestParam int quantity) {
        ProductDto updatedProduct = productService.updateProductStock(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }
    
    /**
     * 특정 상품을 삭제합니다.
     * 
     * @param id 삭제할 상품의 ID
     * @return HTTP 204 No Content 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

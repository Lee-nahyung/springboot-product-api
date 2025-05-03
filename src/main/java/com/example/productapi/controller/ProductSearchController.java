package com.example.productapi.controller;

import com.example.productapi.elasticsearch.document.ProductDocument;
import com.example.productapi.elasticsearch.service.ProductIndexService;
import com.example.productapi.elasticsearch.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;
    private final ProductIndexService productIndexService;

    /**
     * 모든 제품을 엘라스틱서치에 인덱싱합니다.
     */
    @PostMapping("/reindex")
    public ResponseEntity<String> reindexAllProducts() {
        productIndexService.indexAllProducts();
        return ResponseEntity.ok("모든 제품 인덱싱이 완료되었습니다.");
    }

    /**
     * 키워드로 제품 검색 (기본 검색)
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductDocument>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean inStockOnly) {


        // 키워드 검색이 있을 경우 - 이름으로 검색
        if (keyword != null && !keyword.isEmpty()) {
            return ResponseEntity.ok(productSearchService.searchProductsByName(keyword));
        }

        // 카테고리 검색이 있을 경우
        if (category != null && !category.isEmpty()) {
            // 가격 범위 검색도 있는 경우
            if (minPrice != null && maxPrice != null) {
                return ResponseEntity.ok(productSearchService.searchByCategoryAndPriceRange(
                        category, minPrice, maxPrice));
            }
            // 카테고리만 검색
            return ResponseEntity.ok(productSearchService.searchByCategory(category));
        }

        // 가격 범위만 있는 경우
        if (minPrice != null && maxPrice != null) {
            return ResponseEntity.ok(productSearchService.searchByPriceRange(minPrice, maxPrice));
        }

        // 재고 있는 상품만 검색
        if (inStockOnly != null && inStockOnly) {
            return ResponseEntity.ok(productSearchService.searchInStockProducts());
        }

        // 필터가 없으면 모든 상품 반환 (간단히 구현)
        return ResponseEntity.ok((productSearchService.searchAllProducts()));
    }


    /**
     * 이름으로 제품 검색
     */
    @GetMapping("/products/name")
    public ResponseEntity<List<ProductDocument>> searchProductsByName(@RequestParam String keyword) {
        return ResponseEntity.ok(productSearchService.searchProductsByName(keyword));
    }

    /**
     * 카테고리별 제품 검색
     */
    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<ProductDocument>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "none") String priceOrder) {
        
        if ("asc".equals(priceOrder)) {
            return ResponseEntity.ok(productSearchService.searchByCategoryOrderByPriceAsc(category));
        } else if ("desc".equals(priceOrder)) {
            return ResponseEntity.ok(productSearchService.searchByCategoryOrderByPriceDesc(category));
        } else {
            return ResponseEntity.ok(productSearchService.searchByCategory(category));
        }
    }

    /**
     * 가격 범위로 제품 검색
     */
    @GetMapping("/products/price-range")
    public ResponseEntity<List<ProductDocument>> getProductsByPriceRange(
            @RequestParam int min, @RequestParam int max) {
        return ResponseEntity.ok(productSearchService.searchByPriceRange(min, max));
    }

    /**
     * 재고 있는 상품만 검색
     */
    @GetMapping("/products/in-stock")
    public ResponseEntity<List<ProductDocument>> getInStockProducts() {
        return ResponseEntity.ok(productSearchService.searchInStockProducts());
    }
}
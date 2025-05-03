package com.example.productapi.elasticsearch.service;

import com.example.productapi.elasticsearch.document.ProductDocument;
import com.example.productapi.elasticsearch.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;

    /**
     * 모든 제품 검색
     */
    public List<ProductDocument> searchAllProducts() {
        List<ProductDocument> products = new ArrayList<>();
        productSearchRepository.findAll().forEach(products::add);
        return products;

    }

    /**
     * 키워드로 제품 검색 (이름)
     */
    public List<ProductDocument> searchProductsByName(String keyword) {
        return productSearchRepository.findByNameContaining(keyword);
    }

    /**
     * 카테고리별 제품 검색
     */
    public List<ProductDocument> searchByCategory(String category) {
        return productSearchRepository.findByCategoryName(category);
    }

    /**
     * 카테고리별 제품 검색 (가격 오름차순)
     */
    public List<ProductDocument> searchByCategoryOrderByPriceAsc(String category) {
        return productSearchRepository.findByCategoryNameOrderByPriceAsc(category);
    }

    /**
     * 카테고리별 제품 검색 (가격 내림차순)
     */
    public List<ProductDocument> searchByCategoryOrderByPriceDesc(String category) {
        return productSearchRepository.findByCategoryNameOrderByPriceDesc(category);
    }

    /**
     * 가격 범위로 제품 검색
     */
    public List<ProductDocument> searchByPriceRange(int min, int max) {
        return productSearchRepository.findByPriceBetween(min, max);
    }

    /**
     * 카테고리와 가격 범위 결합 검색
     */
    public List<ProductDocument> searchByCategoryAndPriceRange(String category, int min, int max) {
        return productSearchRepository.findByCategoryNameAndPriceBetween(category, min, max);
    }

    /**
     * 재고 있는 상품만 검색
     */
    public List<ProductDocument> searchInStockProducts() {
        return productSearchRepository.findByStockGreaterThan(0);
    }

}
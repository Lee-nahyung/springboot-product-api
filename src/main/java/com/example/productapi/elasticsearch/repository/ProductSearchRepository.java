package com.example.productapi.elasticsearch.repository;

import com.example.productapi.elasticsearch.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

    // 이름으로 검색
    List<ProductDocument> findByNameContaining(String name);
    
    // 카테고리별 검색
    List<ProductDocument> findByCategoryName(String categoryName);
    
    // 가격 범위로 검색
    List<ProductDocument> findByPriceBetween(int minPrice, int maxPrice);
    
    // 카테고리와 가격 범위 결합 검색
    List<ProductDocument> findByCategoryNameAndPriceBetween(String categoryName, int minPrice, int maxPrice);
    
    // 재고 있는 상품만 검색 (stock > 0)
    List<ProductDocument> findByStockGreaterThan(int stock);
    
    // 카테고리별로 가격 오름차순 정렬
    List<ProductDocument> findByCategoryNameOrderByPriceAsc(String categoryName);
    
    // 카테고리별로 가격 내림차순 정렬
    List<ProductDocument> findByCategoryNameOrderByPriceDesc(String categoryName);
}
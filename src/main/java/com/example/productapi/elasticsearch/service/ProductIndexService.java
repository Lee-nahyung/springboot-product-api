package com.example.productapi.elasticsearch.service;

import com.example.productapi.domain.entity.Product;
import com.example.productapi.domain.repository.ProductRepository;
import com.example.productapi.elasticsearch.document.ProductDocument;
import com.example.productapi.elasticsearch.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIndexService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    /**
     * 모든 제품을 엘라스틱서치에 인덱싱합니다.
     */
    @Transactional(readOnly = true)
    public void indexAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDocument> productDocuments = new ArrayList<>();
        
        products.forEach(product -> {
            ProductDocument document = convertToDocument(product);
            productDocuments.add(document);
        });
        
        productSearchRepository.saveAll(productDocuments);
        log.info("전체 제품 인덱싱 완료. 인덱싱된 항목 수: {}", productDocuments.size());
    }

    /**
     * 특정 제품을 엘라스틱서치에 인덱싱합니다.
     */
    public void indexProduct(Product product) {
        ProductDocument document = convertToDocument(product);
        productSearchRepository.save(document);
        log.info("제품 인덱싱 완료: {}", product.getId());
    }

    /**
     * 특정 제품을 엘라스틱서치에서 삭제합니다.
     */
    public void deleteProductFromIndex(Long productId) {
        productSearchRepository.deleteById(productId);
        log.info("제품 인덱스에서 삭제 완료: {}", productId);
    }

    /**
     * JPA 엔티티를 엘라스틱서치 도큐먼트로 변환합니다.
     */
    private ProductDocument convertToDocument(Product product) {
        return ProductDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "미분류")
                .build();
    }
}
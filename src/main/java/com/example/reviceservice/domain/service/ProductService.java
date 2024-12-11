package com.example.reviceservice.domain.service;

import com.example.reviceservice.domain.dto.ProductCreatedRequestDTO;
import com.example.reviceservice.domain.dto.ProductCreatedResponseDTO;
import com.example.reviceservice.domain.entity.Product;
import com.example.reviceservice.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductCreatedResponseDTO createProduct(ProductCreatedRequestDTO productRequestDTO) {
        // DTO를 엔티티로 변환하고 저장
        Product product = productRepository.save(new Product(productRequestDTO));
        return new ProductCreatedResponseDTO(product);
    }


}

package com.example.reviceservice.domain.dto;

import com.example.reviceservice.domain.entity.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreatedResponseDTO {

    private Long id;        // 상품 ID
    private long reviewCount; // 리뷰 개수
    private float score;    // 평균 점수

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modified;

    public ProductCreatedResponseDTO(Product product) {
        this.id = product.getId();
        this.reviewCount = product.getReviewCount();
        this.score = product.getScore();
        this.createdAt = product.getCreated();
        this.modified = product.getModified();
    }

}
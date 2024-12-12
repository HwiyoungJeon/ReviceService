package com.example.reviceservice.domain.entity;

import com.example.reviceservice.domain.dto.ProductCreatedRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Product extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID

    @Column(name = "reviewCount", nullable = false)
    private long reviewCount = 0; // 리뷰 개수

    @Column(name = "score", nullable = false)
    private float score = 0F; // 평균 점수

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public Product(ProductCreatedRequestDTO productRequestDTO) {
        this.reviewCount = 0; // 초기화
        this.score = 0.0f;    // 초기화
    }

    public void updateReviewStats(int reviewCount, float score) {
        this.reviewCount = reviewCount;
        this.score = score;
    }

}

package com.example.reviceservice.domain.entity;

import com.example.reviceservice.domain.dto.ReviewCreatedRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Review extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 리뷰 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 상품 (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member; // 사용자 (FK)

    @Column(name = "review_score", nullable = false)
    private int reviewScore; // 리뷰 점수

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 리뷰 내용

    @Column
    private String imageUrl; // 업로드된 이미지 URL

    public Review(ReviewCreatedRequestDTO reviewCreatedRequestDTO, Product product, Member member) {
        this.product = product;
        this.member = member;
        this.content = reviewCreatedRequestDTO.getContent();
        this.reviewScore = reviewCreatedRequestDTO.getScore();
    }

    public void addImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

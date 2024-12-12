package com.example.reviceservice.domain.util;

import com.example.reviceservice.domain.dto.ReviewResponse;
import com.example.reviceservice.domain.entity.Review;

import java.util.List;

public class ReviewHelper {

    public static ReviewResponse.ReviewDTO convertToReviewDTO(Review review) {
        return new ReviewResponse.ReviewDTO(
                review.getId(),
                review.getMember().getId(),
                review.getReviewScore(),
                review.getContent(),
                review.getImageUrl(),
                review.getCreated()
        );
    }

    public static float calculateAverageScore(List<ReviewResponse.ReviewDTO> reviews) {
        return reviews.isEmpty() ? 0.0F : (float) reviews.stream()
                .mapToDouble(ReviewResponse.ReviewDTO::getScore) // 점수를 double로 처리
                .average()
                .orElse(0.0); // 기본값 double → float 변환
    }
}

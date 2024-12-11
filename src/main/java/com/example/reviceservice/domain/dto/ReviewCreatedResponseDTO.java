package com.example.reviceservice.domain.dto;


import com.example.reviceservice.domain.entity.Review;
import com.example.reviceservice.global.exception.ReviewException;
import com.example.reviceservice.global.message.GlobalMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreatedResponseDTO {

    private long id;
    private Long product_id;
    private Long userId;
    private int score;
    private String content;
    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modified;

    public ReviewCreatedResponseDTO(Review review) {
        this.id = review.getId();
        this.product_id = review.getProduct().getId();
        this.userId = review.getMember().getId();
        if (review.getReviewScore() < 1 || review.getReviewScore() > 5) {
            throw new ReviewException(GlobalMessage.FIVE_AND_ONE_SCORE);
        }
        this.score = review.getReviewScore();
        this.content = review.getContent();
        this.imageUrl = review.getImageUrl();
        this.createdAt = review.getCreated();
        this.modified = review.getModified();
    }
}

package com.example.reviceservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private long totalCount;
    private double score;
    private int cursor;
    private List<ReviewDTO> reviews;

    @Getter
    @AllArgsConstructor
    public static class ReviewDTO {
        private Long id;
        private Long userId;
        private int score;
        private String content;
        private String imageUrl;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }
}
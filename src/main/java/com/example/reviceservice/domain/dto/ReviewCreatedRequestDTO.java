package com.example.reviceservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreatedRequestDTO {

    private Long product_id;
    private Long user_id;
    private int review_score;
    private String content;


}

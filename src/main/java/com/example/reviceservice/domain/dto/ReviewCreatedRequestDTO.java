package com.example.reviceservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreatedRequestDTO {

    private Long userId;

    private int score;

    private String content;
}

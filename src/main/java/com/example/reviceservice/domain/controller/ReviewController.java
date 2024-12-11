package com.example.reviceservice.domain.controller;


import com.example.reviceservice.domain.dto.ReviewCreatedRequestDTO;
import com.example.reviceservice.domain.dto.ReviewCreatedResponseDTO;
import com.example.reviceservice.domain.service.ReviewService;
import com.example.reviceservice.global.handler.SuccessResponse;
import com.example.reviceservice.global.message.GlobalMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<SuccessResponse<ReviewCreatedResponseDTO>> createReview(@RequestBody ReviewCreatedRequestDTO reviewCreatedRequestDTO) {

        ReviewCreatedResponseDTO reviewCreatedResponseDTO = reviewService.createReview(reviewCreatedRequestDTO);
        SuccessResponse<ReviewCreatedResponseDTO> successResponse = new SuccessResponse<>(GlobalMessage.SUCCESS, HttpStatus.OK.value(),reviewCreatedResponseDTO);

        return ResponseEntity.ok(successResponse);
    }


}

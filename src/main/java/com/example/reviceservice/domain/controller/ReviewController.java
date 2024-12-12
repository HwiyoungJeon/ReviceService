package com.example.reviceservice.domain.controller;


import com.example.reviceservice.domain.dto.ProductCreatedResponseDTO;
import com.example.reviceservice.domain.dto.ReviewCreatedRequestDTO;
import com.example.reviceservice.domain.dto.ReviewCreatedResponseDTO;
import com.example.reviceservice.domain.dto.ReviewResponse;
import com.example.reviceservice.domain.service.ReviewService;
import com.example.reviceservice.global.handler.SuccessResponse;
import com.example.reviceservice.global.message.GlobalMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/products/{productId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<ReviewCreatedResponseDTO>> createReview(
            @PathVariable Long productId,
            @RequestPart("review") String reviewJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        ObjectMapper objectMapper = new ObjectMapper();
        ReviewCreatedRequestDTO reviewCreatedRequestDTO;

        try {
            reviewCreatedRequestDTO = objectMapper.readValue(reviewJson, ReviewCreatedRequestDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


        ReviewCreatedResponseDTO reviewCreatedResponseDTO = reviewService.createReview(productId, reviewCreatedRequestDTO, image);

        SuccessResponse<ReviewCreatedResponseDTO> successResponse = new SuccessResponse<>(GlobalMessage.SUCCESS, HttpStatus.OK.value(), reviewCreatedResponseDTO);

        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<SuccessResponse<ReviewResponse>> getReview(
            @PathVariable Long productId,
            @RequestParam(required = false, defaultValue = "0") int cursor,
            @RequestParam(required = false, defaultValue = "10") int size
    ){

        ReviewResponse reviewResponse = reviewService.getReviews(productId, cursor, size);

        SuccessResponse<ReviewResponse> successResponse = new SuccessResponse<>(GlobalMessage.SUCCESS, HttpStatus.OK.value(), reviewResponse);

        return ResponseEntity.ok(successResponse);
    }
}

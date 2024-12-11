package com.example.reviceservice.domain.controller;

import com.example.reviceservice.domain.dto.ProductCreatedRequestDTO;
import com.example.reviceservice.domain.dto.ProductCreatedResponseDTO;
import com.example.reviceservice.domain.service.ProductService;
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
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("")
    public ResponseEntity<SuccessResponse<ProductCreatedResponseDTO>> createProduct(@RequestBody ProductCreatedRequestDTO productRequestDTO) {

        ProductCreatedResponseDTO productResponseDTO = productService.createProduct(productRequestDTO);
        SuccessResponse<ProductCreatedResponseDTO> successResponse = new SuccessResponse<>(GlobalMessage.SUCCESS, HttpStatus.OK.value(),productResponseDTO);

        return ResponseEntity.ok(successResponse);
    }


}

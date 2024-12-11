package com.example.reviceservice.domain.service;

import com.example.reviceservice.domain.dto.ReviewCreatedRequestDTO;
import com.example.reviceservice.domain.dto.ReviewCreatedResponseDTO;
import com.example.reviceservice.domain.entity.Member;
import com.example.reviceservice.domain.entity.Product;
import com.example.reviceservice.domain.entity.Review;
import com.example.reviceservice.domain.repository.MemberRepository;
import com.example.reviceservice.domain.repository.ProductRepository;
import com.example.reviceservice.domain.repository.ReviewRepository;
import com.example.reviceservice.global.exception.MemberException;
import com.example.reviceservice.global.exception.ProductException;
import com.example.reviceservice.global.message.GlobalMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public ReviewCreatedResponseDTO createReview(ReviewCreatedRequestDTO reviewCreatedRequestDTO) {

        // Product ID로 Product 조회
        Product product = productRepository.findById(reviewCreatedRequestDTO.getProduct_id())
                .orElseThrow(() -> new ProductException(GlobalMessage.NOT_FOUND_PRODUCT));

        // Member ID로 Member 조회
        Member member = memberRepository.findById(reviewCreatedRequestDTO.getUser_id())
                .orElseThrow(() -> new MemberException(GlobalMessage.NOT_FOUND_MEMBER));

        // Review 생성
        Review review = new Review(reviewCreatedRequestDTO, product, member);

        Review savedReview = reviewRepository.save(review);

        return new ReviewCreatedResponseDTO(savedReview);
    }
}

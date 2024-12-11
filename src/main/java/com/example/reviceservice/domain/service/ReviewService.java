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
import com.example.reviceservice.global.exception.ReviewException;
import com.example.reviceservice.global.message.GlobalMessage;
import com.example.reviceservice.global.uploader.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ImageUploader imageUploader; // 이미지 업로드 로직 더미 구현체

    @Transactional
    public ReviewCreatedResponseDTO createReview(Long productId, ReviewCreatedRequestDTO reviewCreatedRequestDTO, MultipartFile image) {
        // Product 조회
        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(GlobalMessage.NOT_FOUND_PRODUCT));

        // 사용자 조회
        Member member = memberRepository.findById(reviewCreatedRequestDTO.getUserId())
                .orElseThrow(() -> new MemberException(GlobalMessage.NOT_FOUND_MEMBER));

        String imageUrl = null;
        if (image != null) {
            try {
                imageUrl = imageUploader.upload(image);
            } catch (ReviewException e) {
                throw new ReviewException(e.getMessage());
            }
        }



        // 리뷰 엔티티 생성 및 저장
        Review review = new Review(reviewCreatedRequestDTO, product, member);
        if (imageUrl != null) {
            review.addImage(imageUrl); // 이미지 URL 추가
        }
        reviewRepository.save(review);

        return new ReviewCreatedResponseDTO(review);
    }
}

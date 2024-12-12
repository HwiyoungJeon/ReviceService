package com.example.reviceservice.domain.service;

import com.example.reviceservice.domain.dto.ReviewCreatedRequestDTO;
import com.example.reviceservice.domain.dto.ReviewCreatedResponseDTO;
import com.example.reviceservice.domain.dto.ReviewResponse;
import com.example.reviceservice.domain.entity.Member;
import com.example.reviceservice.domain.entity.Product;
import com.example.reviceservice.domain.entity.Review;
import com.example.reviceservice.domain.repository.MemberRepository;
import com.example.reviceservice.domain.repository.ProductRepository;
import com.example.reviceservice.domain.repository.ReviewRepository;
import com.example.reviceservice.domain.util.ReviewHelper;
import com.example.reviceservice.global.exception.MemberException;
import com.example.reviceservice.global.exception.ProductException;
import com.example.reviceservice.global.exception.ReviewException;
import com.example.reviceservice.global.message.GlobalMessage;
import com.example.reviceservice.global.uploader.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ImageUploader imageUploader; // 이미지 업로드 로직 더미 구현체
    private final RedisReviewService redisReviewService;

    @Transactional
    public ReviewCreatedResponseDTO createReview(Long productId, ReviewCreatedRequestDTO reviewCreatedRequestDTO, MultipartFile image) {
        // Product 조회
        Product product = findProductById(productId);
        // 사용자 조회
        Member member = findMemberById(reviewCreatedRequestDTO.getUserId());
        // 이미지 업로드 처리
        String imageUrl = uploadImage(image);
        // 리뷰 저장
        Review review = saveReview(product, member, reviewCreatedRequestDTO, imageUrl);

        // Redis에 리뷰 수와 점수 업데이트
        updateReviewStatsInRedis(productId, reviewCreatedRequestDTO.getScore());
        syncReviewStatsWithDatabase(productId);
        // 6. MySQL 통계 업데이트
        syncReviewStatsWithDatabase(productId);

        return new ReviewCreatedResponseDTO(review);
    }

    public ReviewResponse getReviews(Long productId, int cursor, int size) {
        // 페이징 요청 생성
        PageRequest pageRequest = createPageRequest(cursor, size);
        Page<Review> reviewsPage = reviewRepository.findByProductId(productId, pageRequest);

        List<ReviewResponse.ReviewDTO> reviews = convertReviewsToDTO(reviewsPage);

        float averageScore = ReviewHelper.calculateAverageScore(reviews);

        // 응답 객체 생성
        return new ReviewResponse(
                reviewsPage.getTotalElements(),
                averageScore,
                cursor,
                reviews
        );

    }

    @Transactional
    public void syncReviewStatsWithDatabase(Long productId) {
        Product product = findProductById(productId);

        // Redis에서 리뷰 수와 총 점수 가져오기
        int reviewCount = redisReviewService.getReviewCount(productId);
        float totalScore = redisReviewService.getTotalScore(productId);
        float averageScore = calculateAverageScore(reviewCount, totalScore);

        // DB에 리뷰 통계 반영
        updateProductStats(product, reviewCount, averageScore);
    }

    private String uploadImage(MultipartFile image) {
        if (image == null) return null;
        try {
            return imageUploader.upload(image);
        } catch (ReviewException e) {
            throw new ReviewException(GlobalMessage.NOT_FOUND_IMAGE);
        }
    }

    private Review saveReview(Product product, Member member, ReviewCreatedRequestDTO requestDTO, String imageUrl) {
        Review review = new Review(requestDTO, product, member);
        if (imageUrl != null) {
            review.addImage(imageUrl);
        }
        return reviewRepository.save(review);
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(GlobalMessage.NOT_FOUND_PRODUCT));
    }

    private Member findMemberById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new MemberException(GlobalMessage.NOT_FOUND_MEMBER));
    }

    private void updateReviewStatsInRedis(Long productId, int score) {
        redisReviewService.incrementReviewCount(productId);
        redisReviewService.incrementTotalScore(productId, score);
    }

    private void updateProductStats(Product product, int reviewCount, float averageScore) {
        product.updateReviewStats(reviewCount, averageScore);
        productRepository.save(product);
    }

    private float calculateAverageScore(int reviewCount, float totalScore) {
        return reviewCount > 0 ? totalScore / reviewCount : 0.0F;
    }

    private PageRequest createPageRequest(int cursor, int size) {
        return PageRequest.of(cursor, size, Sort.by(Sort.Direction.DESC, "created"));
    }

    private List<ReviewResponse.ReviewDTO> convertReviewsToDTO(Page<Review> reviewsPage) {
        return reviewsPage.getContent().stream()
                .map(ReviewHelper::convertToReviewDTO)
                .collect(Collectors.toList());
    }


}

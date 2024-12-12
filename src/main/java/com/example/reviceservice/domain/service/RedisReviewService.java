package com.example.reviceservice.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisReviewService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void incrementReviewCount(Long productId) {
        String key = "product:" + productId + ":count";
        redisTemplate.opsForValue().increment(key, 1);
    }

    public void incrementTotalScore(Long productId, float score) {
        String key = "product:" + productId + ":score";
        redisTemplate.opsForValue().increment(key, score);
    }

    /**
     * Redis에서 리뷰 수 가져오기
     */
    public int getReviewCount(Long productId) {
        String key = "product:" + productId + ":count";
        Object count = redisTemplate.opsForValue().get(key); // Redis에서 값 가져오기
        return count != null ? ((Number) count).intValue() : 0; // 안전한 변환 처리
    }

    /**
     * Redis에서 총 점수 가져오기
     */
    public float getTotalScore(Long productId) {
        String key = "product:" + productId + ":score";
        Object score = redisTemplate.opsForValue().get(key); // Redis에서 값 가져오기
        return score != null ? ((Number) score).floatValue() : 0.0F; // 안전한 변환 처리
    }
}

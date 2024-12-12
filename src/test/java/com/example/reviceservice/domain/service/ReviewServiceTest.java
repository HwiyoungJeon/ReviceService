package com.example.reviceservice.domain.service;

import com.example.reviceservice.domain.dto.ReviewCreatedRequestDTO;
import com.example.reviceservice.domain.dto.ReviewCreatedResponseDTO;
import com.example.reviceservice.domain.entity.Member;
import com.example.reviceservice.domain.entity.Product;
import com.example.reviceservice.domain.repository.MemberRepository;
import com.example.reviceservice.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisReviewService redisReviewService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Product testProduct;
    private Member testMember;

    @BeforeEach
    void setUp() {
        // 테스트용 상품과 사용자 생성
        testProduct = productRepository.save(new Product(1, 0, 0.0F));
        testMember = memberRepository.save(new Member("testUser"));

        // 저장된 데이터 확인
        Product savedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        Member savedMember = memberRepository.findById(testMember.getId()).orElseThrow();

        System.out.println("저장된 상품 ID: " + savedProduct.getId());
        System.out.println("저장된 사용자 ID: " + savedMember.getId());
    }


    /**
     * 단일 리뷰 생성 성공 테스트
     */
    @Test
    @Transactional
    void testCreateReview_Success() {
        ReviewCreatedRequestDTO requestDTO = new ReviewCreatedRequestDTO(testMember.getId(), 5, "좋은 상품입니다!");

        ReviewCreatedResponseDTO responseDTO = reviewService.createReview(testProduct.getId(), requestDTO, null);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getScore()).isEqualTo(5);
        assertThat(responseDTO.getContent()).isEqualTo("좋은 상품입니다!");
    }

    /**
     * 동시성 테스트: 1000개의 동시 요청 처리
     */
    @Test
    void testCreateReview_Concurrency() throws InterruptedException {
        int threadCount = 1000; // 동시 요청 수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 테스트 데이터 미리 저장
        Product product = productRepository.save(testProduct);
        Member member = memberRepository.save(testMember);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    ReviewCreatedRequestDTO requestDTO = new ReviewCreatedRequestDTO(member.getId(), 5, "테스트 리뷰");
                    reviewService.createReview(product.getId(), requestDTO, null);
                } catch (Exception e) {
                    System.err.println("동시성 테스트 중 예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();


        //Redis에서 리뷰 수 확인
        int reviewCount = redisReviewService.getReviewCount(product.getId());
        float totalScore = redisReviewService.getTotalScore(product.getId());

        // MySQL에서 검증
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getReviewCount()).isEqualTo(threadCount);
        assertThat(updatedProduct.getScore()).isEqualTo(5.0F); // 모든 리뷰 점수가 5일 경우
        assertThat(reviewCount).isEqualTo(threadCount);
        assertThat(totalScore).isEqualTo(threadCount * 5);
    }

    /**
     * 멀티스레드에서 락 테스트
     */
    @Test
    void testRedisLockInMultiThread() throws InterruptedException {
        String lockKey = "lock:multi-thread:test";
        RLock lock = redissonClient.getLock(lockKey);

        CountDownLatch latch = new CountDownLatch(2);

        Runnable task1 = () -> {
            try {
                lock.lock(5, TimeUnit.SECONDS);
                System.out.println("스레드 1이 락을 획득했습니다");
                Thread.sleep(2000); // 작업 시뮬레이션
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
                latch.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                if (lock.tryLock(2, 5, TimeUnit.SECONDS)) {
                    System.out.println("스레드 2가 락을 획득했습니다");
                } else {
                    System.out.println("스레드 2가 락 획득에 실패했습니다");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
                latch.countDown();
            }
        };

        // 두 개의 스레드 실행
        new Thread(task1).start();
        new Thread(task2).start();

        latch.await();
        assertFalse(lock.isLocked());
    }

    /**
     * 독립된 락 테스트
     */
    @Test
    void testRedisLockIndependence() {
        String lockKey = "lock:test:" + UUID.randomUUID();
        RLock lock = redissonClient.getLock(lockKey);

        lock.lock(10, TimeUnit.SECONDS);
        assertTrue(lock.isLocked());
        assertTrue(lock.isHeldByCurrentThread());

        lock.unlock();
        assertFalse(lock.isLocked());
    }

    /**
     * 다수의 스레드와 재시도 로직 테스트
     */
    @Test
    void testHighConcurrencyLock() throws InterruptedException {
        int totalThreads = 500; // 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(50); // 최대 50개 스레드 풀
        CountDownLatch latch = new CountDownLatch(totalThreads); // 스레드 완료 대기
        AtomicInteger successCounter = new AtomicInteger(); // 락 획득 성공 카운터
        String lockKey = "lock:performance:test";

        for (int i = 0; i < totalThreads; i++) {
            executorService.execute(() -> {
                RLock lock = redissonClient.getLock(lockKey);
                int retryCount = 10; // 재시도 횟수
                try {
                    boolean acquired = false;
                    for (int j = 0; j < retryCount; j++) {
                        if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                            successCounter.incrementAndGet();
                            acquired = true;
                            Thread.sleep(100); // 작업 시간 시뮬레이션
                            break;
                        } else {
                            Thread.sleep(100); // 재시도 간 대기 시간
                        }
                    }
                    if (!acquired) {
                        System.out.println(Thread.currentThread().getName() + " 재시도 후에도 락 획득에 실패했습니다.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                    latch.countDown();
                }
            });

        }

        latch.await(); // 모든 스레드 완료 대기
        executorService.shutdown();

        System.out.println("총 락 획득 성공 횟수: " + successCounter.get());
        assertEquals(totalThreads, successCounter.get(), "모든 스레드가 순차적으로 락을 획득해야 합니다.");
    }
}

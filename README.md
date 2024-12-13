# 리뷰 서비스 (Review Service)

## 프로젝트 소개
과제 리뷰 서비스는 상품에 대해 평가 및 리뷰 내용을 관리하는 서비스입니다. 유저는 단 하나의 상품에 하나의 리뷰만 작성할 수 있으며, 평가 점수와 리뷰 내용을 가이드로 조회할 수 있습니다.

---

## 기술 스택
- **언어**: Java, Spring Boot
- **DB**: MySQL
- **빌드 도구**: Gradle
- **테스트**: JUnit5

---

## 비즈니스 요구 사항
- 리뷰는 존재하는 상품에만 작성할 수 있습니다.
- 유저는 한 개의 상품에 하나의 리뷰만 작성 가능합니다.
- 리뷰 점수는 1~5점 사이의 점수를 남기는 것이 가능합니다.
- 사진은 선택적으로 업로드 가능합니다.
    - 사진은 S3 에 저장된다고 가정하고, S3 적재 부분은 dummy 구현체를 생성합니다. 
    (실제 S3 연동을 할 필요는 없습니다.
- 리뷰가 작성되면 가장 최근에 작성된 리뷰 순서로 조회됩니다.

---

## **기술적 요구 사항**
- Mysql 조회 시 인덱스를 잘 탈 수 있게 설계해야 합니다.
- 상품 테이블에 reviewCount 와 score 가 잘 반영되어야 한다.
- (Optional) 동시성을 고려한 설계를 해주세요. 많은 유저들이 동시에 리뷰를 작성할 때, 발생할 수 있는 문제를 고려해보세요.
- (Optional) 테스트 코드를 작성하면 좋습니다.

---

## 🔧 프로젝트 구조
    src/ 
      ├── docker/ # Docker 관련 설정 
      ├── main/ 
      │ ├── java/com/example/reviceservice/ │ 
      │ ├── domain/ # 도메인 관련 계층 │ 
      │ │ ├── controller/ # 컨트롤러 계층 │ 
      │ │ ├── dto/ # 데이터 전송 객체 (DTO) │ 
      │ │ ├── entity/ # 엔티티 클래스 │ 
      │ │ ├── repository/ # 데이터베이스 접근 계층 │ 
      │ │ ├── service/ # 서비스 로직 계층 │ 
      │ │ └── util/ # 유틸리티 클래스 │ 
      │ ├── global/ # 전역 설정 및 관리 │ 
      │ │ ├── config/ # 애플리케이션 설정 │ 
      │ │ ├── exception/ # 예외 처리 │ 
      │ │ ├── handler/ # 핸들러 로직 │ 
      │ │ ├── message/ # 메시지 관리 │ 
      │ │ └── uploader/ # 파일 업로드 관련 │ 
      │ └── ReviceServiceApplication # 메인 애플리케이션 실행 파일
      │ └── resources/ │ 
      ├── application.properties # 애플리케이션 설정 파일 
      └── test/ 
      └── java/com/example/reviceservice/ 
      └── domain/service/ # 서비스 계층 테스트
---

## API 명세
| **Param**   | **Description**                                       |
|-------------|-------------------------------------------------------|
| `productId` | 상품 아이디                                           |
| `cursor`    | 커서 값 (직전 조회 API의 응답으로 받은 cursor 값)       |
| `size`      | 조회 사이즈 (기본값 = 10)                             |

### 1. 리뷰 조회 API
- **URL**: `GET /products/{productId}/reviews?cursor={cursor}&size={size}`
- **Request Params**:
  - `productId`: 리뷰를 조회할 상품의 ID
  - `cursor` (optional): 이전 조회의 커서 값
  - `size` (optional): 조회할 리뷰 수 (기본값: 10)
- **Response Body**:
```json
{
  "totalCount": 15, // 해당 상품에 작성된 총리뷰 수
  "score": 4.6,  // 평균 점수
  "cursor": 6,
  "reviews": [
    {
      "id": 15,
      "userId": 1, 작성자 유저 아이디
      "score": 5,
      "content": "이걸 사용하고 인생이 달라졌습니다.",
      "imageUrl": "/image.png",
      "createdAt": "2024-11-25T00:00:00.000Z"
    },
    {
      "id": 14,
      "userId": 3, 작성자 유저 아이디
      "score": 5,
      "content": "이걸 사용하고 제 인생이 달라졌습니다.",
      "imageUrl": null,
      "createdAt": "2024-11-24T00:00:00.000Z"
    }
  ]
}
```
### 2. 리뷰 등록 API

- **URL**: `POST /products/{productId}/reviews`
- **Request Body**:
```json
{
  "userId": 1,
  "score": 4,
  "content": "이걸 사용하고 인생이 달라졌습니다."
}
```

-**Request Part** :
- **이미지 파일**: `MultipartFile` 타입의 단건 이미지 (선택 사항)
- **Response Body**: NONE.
  
---

## 설치 및 실행 방법
- 생략

---

## 데이터베이스 스키마
### Product 테이블
```sql
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reviewCount` bigint NOT NULL DEFAULT '0',
  `score` float NOT NULL DEFAULT '0',
  `created` datetime(6) DEFAULT NULL,
  `modified` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
```
### Member 테이블
``` sql
CREATE TABLE `member` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `modified` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
```
### Review 테이블
```sql
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `review_score` int NOT NULL,
  `content` text NOT NULL,
  `imageUrl` varchar(255) DEFAULT NULL,
  `created` datetime(6) DEFAULT NULL,
  `modified` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  CONSTRAINT `review_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb3;
```
## 🗄️ **ERD (Entity Relationship Diagram)**

아래는 프로젝트의 데이터베이스 설계를 나타낸 ERD(Entity Relationship Diagram)입니다.
<details>
  <summary>🖼️ ERD 보기</summary>
  <br>
  <img src="https://github.com/user-attachments/assets/7d4dc76e-bc70-46dd-a2ea-b86158effc7d" alt="ERD" style="max-width:100%;"/>
</details>

---
## **동작 시연**

### 1. **리뷰 조회 API 호출 예시 (Postman)**
<details>
  <summary>🖼️ 리뷰 조회 API 보기</summary>
  <br>
  <img src="https://github.com/user-attachments/assets/5efb4e8a-7e69-42e8-94c7-d8001df74a37" alt="리뷰 조회 API" style="max-width:100%;"/>
</details>


### 2. **리뷰 등록 API 호출 예시 (Postman)**
<details>
  <summary>🖼️ 리뷰 등록 API 보기</summary>
  <br>
  <img src="https://github.com/user-attachments/assets/866f9b74-37c7-4cbb-b0e8-41ceeea05545" alt="리뷰 등록 API" style="max-width:100%;"/>
</details>

## Redis 실행 화면

다음은 Redis CLI를 통해 리뷰 데이터가 저장된 모습을 확인한 장면입니다:
| **키값**   | **설명**                                       |
|-------------|-------------------------------------------------------|
| `product:{productId}:count` | 특정 상품의 리뷰 개수를 저장합니다.  |
| `product:{productId}:score`    |특정 상품의 총 점수를 저장합니다.      |

<details>
  <summary>🖼️ Redis 실행 화면 보기</summary>
  <br>
  <img src="https://github.com/user-attachments/assets/040f7e1d-3612-4771-8a68-77fd5d1b1986" alt="Redis 실행 화면" style="max-width:100%;"/>
</details>



---
## 🔥 **트러블슈팅**
### 1️⃣ **문제: 동시성 문제로 인한 리뷰 카운트 및 총 점수 계산 오류**
- **상황**: 여러 사용자가 동시에 리뷰를 작성할 때, **reviewCount**와 **총 점수(score)**가 잘못 업데이트되는 문제 발생.
- **원인**: **동시성 제어**가 없어서 두 개의 트랜잭션이 동시에 실행되면서 **데이터 정합성**이 깨짐.
---
### 🔍 **해결 방법**
#### ✅ **Redis를 활용한 동시성 제어**
- **Redis의 Atomic 연산**을 활용하여 동시성을 관리.
- `reviewCount`와 `score`를 **Redis에 캐싱**하여 동시성 문제를 해결.
- Redis의 **increment() 메서드**를 사용해 원자적 연산(Atomic Operation)으로 카운트 및 점수를 처리.
---
### 💡 **주요 구현 사항**
1. **리뷰 카운트 증가**
   - Redis의 `increment()` 메서드를 통해 **원자적으로 카운트를 증가**시킴.
2. **총 점수 증가**
   - 리뷰 작성 시, 리뷰 점수를 **increment() 메서드로 누적**.
3. **캐시에서 데이터 조회**
   - Redis에 저장된 **카운트와 점수**를 조회하여 MySQL에 반영.
---
### 🛠️ **핵심 코드**
```java
@Service
@RequiredArgsConstructor
public class RedisReviewService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 리뷰 카운트 증가 (Atomic 연산)
    public void incrementReviewCount(Long productId) {
        String key = "product:" + productId + ":count";
        redisTemplate.opsForValue().increment(key, 1);
    }

    // 리뷰 점수 증가 (Atomic 연산)
    public void incrementTotalScore(Long productId, float score) {
        String key = "product:" + productId + ":score";
        redisTemplate.opsForValue().increment(key, score);
    }

    // Redis에서 리뷰 카운트 가져오기
    public int getReviewCount(Long productId) {
        String key = "product:" + productId + ":count";
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? ((Number) count).intValue() : 0;
    }

    // Redis에서 총 점수 가져오기
    public float getTotalScore(Long productId) {
        String key = "product:" + productId + ":score";
        Object score = redisTemplate.opsForValue().get(key);
        return score != null ? ((Number) score).floatValue() : 0.0F;
    }
}
```
---
## 향후 개선점
- **리뷰 수정/삭제 기능**: 유저가 작성한 리뷰를 수정하거나 삭제할 수 있도록 추가 구현
- **페이징 최적화 및 동시성 처리**: 대량의 리뷰를 조회할 때 성능 최적화
  -  CPU 성능 부족으로 인해 **최대 500명의 동시 사용자**를 처리할 수 있도록 구현됨
  -  추후 최대 **최대 3000명의 동시 사용자**를 처리할 수 있도록 개선
- **S3 실제 연동**: 파일 업로드를 실제 S3와 연동하여 실제 환경에서도 동작하도록 개선
- **배포 자동화**: Jenkins를 사용해 CI/CD 파이프라인을 구축하여 지속적인 배포 환경을 구성
- **읽기/쓰기 분리**: 데이터베이스의 읽기 부하와 쓰기 부하를 분리하여 성능 향상
- **Kafka 도입**: 리뷰 작성 시 비동기 처리를 위한 메시지 큐 시스템 추가






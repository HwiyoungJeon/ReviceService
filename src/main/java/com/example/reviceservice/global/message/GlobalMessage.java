package com.example.reviceservice.global.message;


public class GlobalMessage {

    public static final String SUCCESS = "정상적으로 실행 되었습니다.";
    public static final String NOT_FOUND_PRODUCT = "해당 상품이 존재하지 않습니다.";
    public static final String NOT_FOUND_MEMBER = "해당 회원이 존재하지 않습니다.";
    public static final String FIVE_AND_ONE_SCORE = "리뷰 스코어를 1~5사이로 설정해주세요 ";
    public static final String NOT_FOUND_IMAGE = "업로드된 이미지가 존재하지 않습니다.";
    public static final String DELETED = "정상적으로 삭제 되었습니다.";

    // Redis 락 관련 메시지
    public static final String LOCK_FAILED = "리뷰 작성한 인원의 요청증가로 잠시 후 다시 시도해주세요.";
    public static final String LOCK_ACQUISITION_FAILED = "락 획득에 실패했습니다.";

}

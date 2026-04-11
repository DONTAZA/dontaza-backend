package com.dontaza.dontazabackend.global.response;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Server Error
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "S01", "예상치 못한 서버 에러가 발생하였습니다"),

    // Global Error
    RESOURCE_NOT_FOUND(NOT_FOUND, "G01", "요청한 자원을 찾을 수 없습니다"),
    NOT_SUPPORTED(BAD_REQUEST, "G02", "지원하지 않는 요청입니다"),
    RESOURCE_EXISTS(BAD_REQUEST, "G03", "이미 존재하는 자원입니다"),
    INVALID_INPUT_VALUE(BAD_REQUEST, "G04", "유효하지 않은 입력값입니다"),

    // Station Error
    STATION_NOT_FOUND(NOT_FOUND, "ST01", "존재하지 않는 대여소 번호입니다"),
    TOO_FAR_FROM_STATION(BAD_REQUEST, "ST02", "대여소에서 50m 초과 위치입니다"),

    // Riding Error
    ALREADY_RIDING(BAD_REQUEST, "R01", "이미 진행 중인 라이딩이 존재합니다"),
    RIDING_NOT_FOUND(NOT_FOUND, "R02", "해당 라이딩 세션을 찾을 수 없습니다"),
    RIDING_NOT_VERIFIED(BAD_REQUEST, "R03", "대여 검증이 완료되지 않았습니다"),
    RIDING_ALREADY_ENDED(BAD_REQUEST, "R04", "이미 종료된 라이딩입니다"),
    RIDING_TOO_SHORT(BAD_REQUEST, "R05", "최소 5분 이상 라이딩해야 합니다"),

    // Infrastructure Error
    EXTERNAL_API_FAIL(INTERNAL_SERVER_ERROR, "I01", "외부 API 처리 작업을 실패하였습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

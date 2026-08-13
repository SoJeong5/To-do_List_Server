package com.example.todoapp.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(
            HttpStatus.BAD_REQUEST,
            "존재하지 않는 사용자입니다."
    ),

    LOGIN_ID_DUPLICATED(
            HttpStatus.BAD_REQUEST,
            "아이디 확인 상태가 변경되었습니다. 다시 중복확인 해주세요." // "사용할 수 없는 아이디입니다. 다시 확인해주세요."
    ),

    NICKNAME_DUPLICATED(
            HttpStatus.BAD_REQUEST,
            "닉네임 확인 상태가 변경되었습니다. 다시 중복확인 해주세요."
    ),

    LOGIN_FAILED(
            HttpStatus.BAD_REQUEST,
            "아이디 또는 비밀번호가 올바르지 않습니다."
    );




    private final HttpStatus status;
    private final String message;
}

package com.example.todoapp.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data; // 응답 데이터가 상황마다 다르기 때문에 제너릭 <T> 사용
}

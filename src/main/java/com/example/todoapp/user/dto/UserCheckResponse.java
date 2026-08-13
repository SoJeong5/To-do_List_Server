package com.example.todoapp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCheckResponse {
    private boolean available;
    private String message;
}

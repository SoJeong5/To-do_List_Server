package com.example.todoapp.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String loginId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private LocalDate birth;
}

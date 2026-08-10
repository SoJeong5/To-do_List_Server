package com.example.todoapp.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String loginId;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private LocalDate birth;

}

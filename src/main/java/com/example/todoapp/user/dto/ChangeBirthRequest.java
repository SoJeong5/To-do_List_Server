package com.example.todoapp.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ChangeBirthRequest {
    private LocalDate birth;
}

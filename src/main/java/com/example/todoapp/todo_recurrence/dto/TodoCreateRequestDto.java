package com.example.todoapp.todo_recurrence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class TodoCreateRequestDto {
    private Long userId;
    private String title;
    private LocalTime time;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type; // 일반, 기한, 반복

    // 반복 설정 옵션 (반복일 경우에만 전달)
    private Boolean isRepeat;
    private String recurrenceType; // DAILY, WEEKLY, MONTHLY
    private LocalDate recurrenceEndDate;
}
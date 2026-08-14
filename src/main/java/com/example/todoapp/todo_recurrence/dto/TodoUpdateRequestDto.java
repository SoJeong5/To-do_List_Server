package com.example.todoapp.todo_recurrence.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class TodoUpdateRequestDto {
    private String title;
    private LocalTime time;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;

    private Boolean isRepeat;
    private String recurrenceType;
    private LocalDate recurrenceEndDate;
}
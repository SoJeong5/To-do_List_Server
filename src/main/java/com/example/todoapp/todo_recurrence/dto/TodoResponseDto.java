package com.example.todoapp.todo_recurrence.dto;

import com.example.todoapp.todo_recurrence.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalTime time;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private Boolean isCompleted;
    private Integer displayOrder;

    // 반복 옵션 정보
    private String recurrenceType;

    // Todo 엔티티를 DTO로 변환하는 static 메서드
    public static TodoResponseDto from(Todo todo) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .userId(todo.getUserId())
                .title(todo.getTitle())
                .time(todo.getTime())
                .startDate(todo.getStartDate())
                .endDate(todo.getEndDate())
                .type(todo.getType())
                .isCompleted(todo.getIsCompleted())
                .displayOrder(todo.getDisplayOrder())
                .recurrenceType(todo.getRecurrence() != null ? todo.getRecurrence().getRecurrenceType() : null)
                .build();
    }
}
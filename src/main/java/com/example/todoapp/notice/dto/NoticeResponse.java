package com.example.todoapp.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NoticeResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}

package com.example.todoapp.todo_recurrence.controller;

import com.example.todoapp.todo_recurrence.dto.TodoCreateRequestDto;
import com.example.todoapp.todo_recurrence.dto.TodoResponseDto;
import com.example.todoapp.todo_recurrence.dto.TodoUpdateRequestDto;
import com.example.todoapp.todo_recurrence.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    //특정 날짜의 To-do 목록 조회
    @GetMapping
    public ResponseEntity<List<TodoResponseDto>> getTodosByDate(
            @RequestParam("userId") Long userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(todoService.getTodosForDate(userId, date));
    }

    //To-do 생성
    @PostMapping
    public ResponseEntity<TodoResponseDto> createTodo(@RequestBody TodoCreateRequestDto dto) {
        return ResponseEntity.ok(todoService.createTodo(dto));
    }

    //완료 여부 체크 토글
    @PatchMapping("/{id}/check")
    public ResponseEntity<Void> toggleCheck(@PathVariable("id") Long id) {
        todoService.toggleCheck(id);
        return ResponseEntity.ok().build();
    }

    //수정 및 삭제
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponseDto> updateTodo(@PathVariable("id") Long id, @RequestBody TodoUpdateRequestDto dto) {
        return ResponseEntity.ok(todoService.updateTodo(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable("id") Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
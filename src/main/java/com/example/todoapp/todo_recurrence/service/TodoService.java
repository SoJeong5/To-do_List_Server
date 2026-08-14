package com.example.todoapp.todo_recurrence.service;

import com.example.todoapp.todo_recurrence.dto.TodoCreateRequestDto;
import com.example.todoapp.todo_recurrence.dto.TodoResponseDto;
import com.example.todoapp.todo_recurrence.dto.TodoUpdateRequestDto;
import com.example.todoapp.todo_recurrence.entity.Recurrence;
import com.example.todoapp.todo_recurrence.entity.Todo;
import com.example.todoapp.todo_recurrence.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;

    // 1. 특정 날짜의 Todo 목록 조회
    @Transactional(readOnly = true)
    public List<TodoResponseDto> getTodosForDate(Long userId, LocalDate date) {
        // 1. 해당 유저의 모든 Todo 또는 해당 기간 범위에 걸쳐있는 Todo 조회
        List<Todo> allTodos = todoRepository.findAllByUserId(userId);

        // 2. 조회하려는 날짜(date)에 보여줘야 하는 Todo만 필터링
        List<Todo> filteredTodos = allTodos.stream()
                .filter(todo -> isTodoVisibleOnDate(todo, date))
                .toList();

        return filteredTodos.stream()
                .map(TodoResponseDto::from)
                .collect(Collectors.toList());
    }

    // 📌 핵심: 날짜별 반복 조건 체크 메서드
    private boolean isTodoVisibleOnDate(Todo todo, LocalDate date) {
        LocalDate startDate = todo.getStartDate();
        LocalDate endDate = todo.getEndDate();

        // 1. 기본 범위 조건 check (시작일 이전이거나 종료일 이후면 표시 안 함)
        if (date.isBefore(startDate) || date.isAfter(endDate)) {
            return false;
        }

        // 2. 반복 설정이 없는 일반/기한 Todo인 경우
        if (todo.getRecurrence() == null) {
            return true;
        }

        // 3. 반복 옵션(recurrenceType)에 따른 조건 계산
        String recurrenceType = todo.getRecurrence().getRecurrenceType();

        switch (recurrenceType) {
            case "DAILY":
                // 매일 반복: 기간 내 모든 날짜 표시
                return true;

            case "WEEKLY":
                // 매주 반복: 시작 날짜의 '요일'과 조회 날짜의 '요일'이 같을 때만 표시
                // 예: 시작일이 월요일이면 매주 월요일만 true
                return date.getDayOfWeek() == startDate.getDayOfWeek();

            case "MONTHLY":
                // 매월 반복: 시작 날짜의 '일(Day)'과 조회 날짜의 '일(Day)'이 같을 때만 표시
                // 예: 시작일이 15일이면 매월 15일만 true
                return date.getDayOfMonth() == startDate.getDayOfMonth();

            default:
                return true;
        }
    }

    // 2. Todo 생성
    public TodoResponseDto createTodo(TodoCreateRequestDto dto) {
        Todo todo = new Todo();
        todo.setUserId(dto.getUserId());
        todo.setTitle(dto.getTitle());
        todo.setTime(dto.getTime());
        todo.setStartDate(dto.getStartDate());
        todo.setEndDate(dto.getEndDate());
        todo.setType(dto.getType());

        if (Boolean.TRUE.equals(dto.getIsRepeat())) {
            Recurrence recurrence = new Recurrence();
            recurrence.setRecurrenceType(dto.getRecurrenceType());
            recurrence.setEndDate(dto.getRecurrenceEndDate() != null ? dto.getRecurrenceEndDate() : dto.getEndDate());

            todo.setRecurrence(recurrence);
        }

        Todo savedTodo = todoRepository.save(todo);
        return TodoResponseDto.from(savedTodo);
    }

    // 3. 완료 상태 토글 (isCompleted 반전)
    public void toggleCheck(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 To-do를 찾을 수 없습니다. id=" + id));

        Boolean currentStatus = todo.getIsCompleted();
        todo.setIsCompleted(currentStatus == null ? true : !currentStatus);
    }

    // 4. Todo 수정
    public TodoResponseDto updateTodo(Long id, TodoUpdateRequestDto dto) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 To-do를 찾을 수 없습니다. id=" + id));

        todo.setTitle(dto.getTitle());
        todo.setTime(dto.getTime());
        todo.setStartDate(dto.getStartDate());
        todo.setEndDate(dto.getEndDate());
        todo.setType(dto.getType());

        // 반복 설정 업데이트
        if (Boolean.TRUE.equals(dto.getIsRepeat())) {
            if (todo.getRecurrence() != null) {
                todo.getRecurrence().setRecurrenceType(dto.getRecurrenceType());
                todo.getRecurrence().setEndDate(dto.getRecurrenceEndDate() != null ? dto.getRecurrenceEndDate() : dto.getEndDate());
            } else {
                Recurrence recurrence = new Recurrence();
                recurrence.setRecurrenceType(dto.getRecurrenceType());
                recurrence.setEndDate(dto.getRecurrenceEndDate() != null ? dto.getRecurrenceEndDate() : dto.getEndDate());
                todo.setRecurrence(recurrence);
            }
        } else {
            // 반복 해제 시 Recurrence 삭제 (orphanRemoval = true에 의해 DB에서도 삭제됨)
            todo.setRecurrence(null);
        }

        return TodoResponseDto.from(todo);
    }

    // 5. Todo 삭제
    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 To-do를 찾을 수 없습니다. id=" + id));

        todoRepository.delete(todo);
    }
}
package com.example.todoapp.todo_recurrence.service;

import com.example.todoapp.todo_recurrence.dto.TodoCreateRequestDto;
import com.example.todoapp.todo_recurrence.dto.TodoResponseDto;
import com.example.todoapp.todo_recurrence.dto.TodoUpdateRequestDto;
import com.example.todoapp.todo_recurrence.entity.Todo;
import com.example.todoapp.todo_recurrence.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public List<TodoResponseDto> getTodosForDate(Long userId, LocalDate date) {
        List<Todo> allTodos = todoRepository.findAllByUserId(userId);

        List<Todo> filteredTodos = allTodos.stream()
                .filter(todo -> isTodoVisibleOnDate(todo, date))
                .toList();

        return filteredTodos.stream()
                .map(TodoResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<String> getCompletedDatesForMonth(Long userId, int year, int month){
        List<Todo> allTodos = todoRepository.findAllByUserId(userId);
        List<String> completedDates = new ArrayList<>();

        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        for(LocalDate day = startOfMonth; !day.isAfter(endOfMonth); day = day.plusDays(1)){
            final LocalDate currentDate = day;

            List<Todo> todosOnDate = allTodos.stream()
                    .filter(todo -> isTodoVisibleOnDate(todo, currentDate))
                    .toList();

            if(!todosOnDate.isEmpty() && todosOnDate.stream().allMatch((t -> Boolean.TRUE.equals(t.getIsCompleted())))){
                completedDates.add(currentDate.toString());
            }
        }

        return completedDates;
    }

    private boolean isTodoVisibleOnDate(Todo todo, LocalDate date) {
        LocalDate startDate = todo.getStartDate();
        LocalDate endDate = todo.getEndDate();

        // 날짜 범위 벗어나면 제외
        if (date.isBefore(startDate) || date.isAfter(endDate)) {
            return false;
        }

        // 개별 생성 구조 및 일반/기한 Todo 노출
        if (todo.getRecurrence() == null) {
            return true;
        }

        // (기존 레거시 데이터 호환용)
        String recurrenceType = todo.getRecurrence().getRecurrenceType();
        return switch (recurrenceType) {
            case "DAILY" -> true;
            case "WEEKLY" -> date.getDayOfWeek() == startDate.getDayOfWeek();
            case "MONTHLY" -> date.getDayOfMonth() == startDate.getDayOfMonth();
            default -> true;
        };
    }

    public TodoResponseDto createTodo(TodoCreateRequestDto dto) {
        if(Boolean.TRUE.equals(dto.getIsRepeat())){
            LocalDate start = dto.getStartDate();
            LocalDate end = dto.getEndDate();

            String recurrenceType = dto.getRecurrenceType() != null ? dto.getRecurrenceType() : "DAILY";
            Todo lastSavedTodo = null;

            while(!start.isAfter(end)){
                boolean shouldCreate = switch(recurrenceType){
                    case "DAILY" -> true;
                    case "WEEKLY" -> start.getDayOfWeek() == dto.getStartDate().getDayOfWeek();
                    case "MONTHLY" -> start.getDayOfMonth() == dto.getStartDate().getDayOfMonth();
                    default -> true;
                };

                if(shouldCreate){
                    Todo todo = new Todo();

                    todo.setUserId(dto.getUserId());
                    todo.setTitle(dto.getTitle());
                    todo.setTime(dto.getTime());

                    // 날짜별 독립 객체 생성 (하루 단위 고정)
                    todo.setStartDate(start);
                    todo.setEndDate(start);
                    todo.setType(dto.getType());
                    todo.setIsCompleted(false);

                    lastSavedTodo = todoRepository.save(todo);
                }

                start = start.plusDays(1);
            }

            return TodoResponseDto.from(lastSavedTodo);
        }
        else{
            Todo todo = new Todo();
            todo.setUserId(dto.getUserId());
            todo.setTitle(dto.getTitle());
            todo.setTime(dto.getTime());
            todo.setStartDate(dto.getStartDate());
            todo.setEndDate(dto.getEndDate());
            todo.setType(dto.getType());
            todo.setIsCompleted(false);

            Todo savedTodo = todoRepository.save(todo);

            return TodoResponseDto.from(savedTodo);
        }
    }

    public void toggleCheck(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 To-do를 찾을 수 없습니다. id=" + id));

        Boolean currentStatus = todo.getIsCompleted();
        todo.setIsCompleted(currentStatus == null || !currentStatus);
    }

    public TodoResponseDto updateTodo(Long id, TodoUpdateRequestDto dto) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 To-do를 찾을 수 없습니다. id=" + id));

        String oldTitle = todo.getTitle();
        Long userId = todo.getUserId();
        LocalDate currentStartDate = todo.getStartDate(); // 수정 시도한 항목의 날짜

        // 1. 반복 Todo인 경우: 오늘(현재 수정 날짜) 포함 이후의 기존 반복 Todo들을 정리/재생성
        if ("REPEAT".equals(todo.getType()) || Boolean.TRUE.equals(dto.getIsRepeat())) {

            // ① 기존에 생성되어 있던 '현재 날짜 포함 이후' 동일 반복 Todo들을 DB에서 모두 삭제
            List<Todo> futureTodos = todoRepository.findAllByUserId(userId).stream()
                    .filter(t -> "REPEAT".equals(t.getType()))
                    .filter(t -> oldTitle.equals(t.getTitle()))
                    .filter(t -> !t.getStartDate().isBefore(currentStartDate))
                    .toList();

            todoRepository.deleteAll(futureTodos); // 싹 지워서 범위 벗어난 항목 제거!

            // ② 새로운 기간(dto.getStartDate() ~ dto.getEndDate()) 및 조건으로 새로 생성
            LocalDate start = dto.getStartDate();
            LocalDate end = dto.getEndDate();
            String recurrenceType = dto.getRecurrenceType() != null ? dto.getRecurrenceType() : "DAILY";
            Todo lastSavedTodo = null;

            while (!start.isAfter(end)) {
                boolean shouldCreate = switch (recurrenceType) {
                    case "DAILY" -> true;
                    case "WEEKLY" -> start.getDayOfWeek() == dto.getStartDate().getDayOfWeek();
                    case "MONTHLY" -> start.getDayOfMonth() == dto.getStartDate().getDayOfMonth();
                    default -> true;
                };

                if (shouldCreate) {
                    Todo newTodo = new Todo();
                    newTodo.setUserId(userId);
                    newTodo.setTitle(dto.getTitle());
                    newTodo.setTime(dto.getTime());
                    newTodo.setStartDate(start);
                    newTodo.setEndDate(start);
                    newTodo.setType("REPEAT");
                    newTodo.setIsCompleted(false);

                    lastSavedTodo = todoRepository.save(newTodo);
                }
                start = start.plusDays(1);
            }

            return TodoResponseDto.from(lastSavedTodo != null ? lastSavedTodo : todo);
        }

        // 2. 일반 / 기한 Todo 수정인 경우
        else {
            todo.setTitle(dto.getTitle());
            todo.setTime(dto.getTime());
            todo.setStartDate(dto.getStartDate());
            todo.setEndDate(dto.getEndDate());
            todo.setType(dto.getType());

            return TodoResponseDto.from(todo);
        }
    }

    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 To-do를 찾을 수 없습니다. id=" + id));

        todoRepository.delete(todo);
    }
}
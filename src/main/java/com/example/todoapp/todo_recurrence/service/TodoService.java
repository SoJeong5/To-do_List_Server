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

        if (date.isBefore(startDate) || date.isAfter(endDate)) {
            return false;
        }

        if (todo.getRecurrence() == null) {
            return true;
        }

        String recurrenceType = todo.getRecurrence().getRecurrenceType();

        return switch (recurrenceType) {
            case "DAILY" -> true;
            case "WEEKLY" -> date.getDayOfWeek() == startDate.getDayOfWeek();
            case "MONTHLY" -> date.getDayOfMonth() == startDate.getDayOfMonth();
            default -> true;
        };
    }

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

    public void toggleCheck(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 To-do를 찾을 수 없습니다. id=" + id));

        Boolean currentStatus = todo.getIsCompleted();
        todo.setIsCompleted(currentStatus == null || !currentStatus);
    }

    public TodoResponseDto updateTodo(Long id, TodoUpdateRequestDto dto) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 To-do를 찾을 수 없습니다. id=" + id));

        todo.setTitle(dto.getTitle());
        todo.setTime(dto.getTime());
        todo.setStartDate(dto.getStartDate());
        todo.setEndDate(dto.getEndDate());
        todo.setType(dto.getType());

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
            todo.setRecurrence(null);
        }

        return TodoResponseDto.from(todo);
    }

    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 To-do를 찾을 수 없습니다. id=" + id));

        todoRepository.delete(todo);
    }
}
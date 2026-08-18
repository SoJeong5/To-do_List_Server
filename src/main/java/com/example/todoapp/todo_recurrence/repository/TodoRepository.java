package com.example.todoapp.todo_recurrence.repository;

import com.example.todoapp.todo_recurrence.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}


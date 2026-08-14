package com.example.todoapp.todo_recurrence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "todo")
@Getter @Setter
@NoArgsConstructor
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    private LocalTime time;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String type = "일반";

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 1;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Recurrence recurrence;

    public void setRecurrence(Recurrence recurrence){
        this.recurrence = recurrence;

        if(recurrence != null){
            recurrence.setTodo(this);
        }
    }
}
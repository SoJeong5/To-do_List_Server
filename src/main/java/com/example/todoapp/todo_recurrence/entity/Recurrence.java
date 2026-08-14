package com.example.todoapp.todo_recurrence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "recurrence")
@Getter
@Setter
@NoArgsConstructor
public class Recurrence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    @Column(name = "recurrence_type")
    private String recurrenceType;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
}
package com.example.todoapp.notice.repository;

import com.example.todoapp.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}

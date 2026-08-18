package com.example.todoapp.notice.service;

import com.example.todoapp.notice.dto.NoticeResponse;
import com.example.todoapp.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeResponse> getNotices() {

        return noticeRepository.findAll()
                .stream()
                .map(notice -> new NoticeResponse(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getCreatedAt()
                ))
                .toList();
    }
}

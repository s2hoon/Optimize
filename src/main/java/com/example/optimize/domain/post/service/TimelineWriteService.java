package com.example.optimize.domain.post.service;


import com.example.optimize.domain.post.entity.Timeline;
import com.example.optimize.domain.post.repository.TimelineRepository;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service

public class TimelineWriteService {

    private final TimelineRepository timelineRepository;

    public void deliveryToTimeLine(Long postId, List<Long> toMemberIds) throws SQLException {
        var timeLines = toMemberIds.stream()
                .map((memberId) -> toTimeLine(postId, memberId))
                .toList();
        timelineRepository.bulkInsert(timeLines);
    }

    private Timeline toTimeLine(Long postId, Long memberId) {
        return Timeline
                .builder()
                .memberId(memberId)
                .postId(postId)
                .build();
    }

}

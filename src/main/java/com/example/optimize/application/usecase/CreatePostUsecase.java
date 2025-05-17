package com.example.optimize.application.usecase;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.example.optimize.domain.follow.entity.Follow;
import com.example.optimize.domain.follow.service.FollowReadService;
import com.example.optimize.domain.post.dto.PostCommand;
import com.example.optimize.domain.post.service.PostWriteService;
import com.example.optimize.domain.post.service.TimelineWriteService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreatePostUsecase {
    final private PostWriteService postWriteService;
    final private FollowReadService followReadService;
    final private TimelineWriteService timelineWriteService;


    public Long execute(PostCommand command) throws SQLException {
        var postId = postWriteService.create(command);

        var followerMemberIds = followReadService
                .getFollowers(command.memberId()).stream()
                .map((Follow::getFromMemberId))
                .toList();

        timelineWriteService.deliveryToTimeLine(postId, followerMemberIds);

        return postId;
    }
}


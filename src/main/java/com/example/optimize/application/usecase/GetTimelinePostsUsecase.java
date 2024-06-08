package com.example.optimize.application.usecase;

import com.example.optimize.domain.follow.entity.Follow;
import com.example.optimize.domain.follow.service.FollowReadService;
import com.example.optimize.domain.post.entity.Post;
import com.example.optimize.domain.post.service.PostReadService;
import com.example.optimize.util.CursorRequest;
import com.example.optimize.util.PageCursor;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetTimelinePostsUsecase {

    private final FollowReadService followReadService;
    private final PostReadService postReadService;

    public PageCursor<Post> execute(Long memberId, CursorRequest cursorRequest) throws SQLException {
        //1.memberId 로 follow 조회
        //2. 1번 결과로 게시물 조회
        var followings = followReadService.getFollowings(memberId);
        var followingsMemberIds = followings.stream().map(Follow::getToMemberId).toList();
        var posts = postReadService.getPost(followingsMemberIds, cursorRequest);
        return posts;
    }
}

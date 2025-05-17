package com.example.optimize.application.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.optimize.application.usecase.CreatePostLikeUsecase;
import com.example.optimize.application.usecase.CreatePostUsecase;
import com.example.optimize.application.usecase.GetTimelinePostsUsecase;
import com.example.optimize.domain.post.dto.DailyPostCount;
import com.example.optimize.domain.post.dto.DailyPostCountRequest;
import com.example.optimize.domain.post.dto.PostCommand;
import com.example.optimize.domain.post.entity.Post;
import com.example.optimize.domain.post.service.PostReadService;
import com.example.optimize.domain.post.service.PostWriteService;
import com.example.optimize.util.CursorRequest;
import com.example.optimize.util.PageCursor;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostWriteService postWriteService;
    private final PostReadService postReadService;
    private final GetTimelinePostsUsecase getTimelinePostsUsecase;
    private final CreatePostUsecase createPostUsecase;
    private final CreatePostLikeUsecase createPostLikeUsecase;
    @PostMapping("")
    public Long create(@RequestBody PostCommand command) throws SQLException {
//        return postWriteService.create(command);
        return createPostUsecase.execute(command);
    }

    @GetMapping("/daily-post-counts")
    public List<DailyPostCount> getDailyPostCounts(DailyPostCountRequest request) throws SQLException {
        return postReadService.getDailyPostCounts(request);
    }

    @GetMapping("/members/{memberId}")
    public Page<Post> getPosts(@PathVariable Long memberId,
                               @RequestParam Integer page,
                               @RequestParam Integer size) throws SQLException {
        return postReadService.getPost(memberId, PageRequest.of(page, size));
    }

    @GetMapping("/members/{memberId}/by-cursor")
    public PageCursor<Post> getPostsByCursor(@PathVariable Long memberId,
                                             CursorRequest cursorRequest
    ) throws SQLException {
        return postReadService.getPost(memberId, cursorRequest);
    }

    @GetMapping("/member/{memberId}/timeline")
    public PageCursor<Post> getTimeline(@PathVariable Long memberId, CursorRequest cursorRequest) throws SQLException {
        return getTimelinePostsUsecase.executeByTimeLine(memberId, cursorRequest);
    }

    // 비관적 락을 사용하여 좋아요 처리
    // - 트랜잭션 시작 시점에 row 에 락을 걸어 다른 트랜잭션의 수정/읽기를 차단
    // - 충돌을 사전에 방지하지만, 락 경합이 심할 경우 성능 저하 가능
    @PostMapping("/{postId}/like/pessimistic")
    public void likePost(@PathVariable  Long postId) throws SQLException {
        postWriteService.likePostPessimisticLock(postId);
    }
    // 낙관적 락을 사용하여 좋아요 처리
    // - 락을 걸지 않고 동작하다가 커밋 시점에 version 필드로 충돌 여부 확인
    // - 충돌 일어날시, 커밋 재시도
    // - 충돌 가능성이 낮을 경우 성능이 뛰어남
    @PostMapping("/{postId}/like/optimistic")
    public void likePostOptimistic(@PathVariable  Long postId) throws SQLException {
        postWriteService.likePostOptimisticLock(postId);
    }
    // 별도 PostLike 테이블을 사용하여 좋아요 기록
    // - 사용자의 좋아요 기록을 INSERT 방식으로 저장 (동시성 이슈 없음)
    // - likeCount는 집계 쿼리 또는 배치 처리로 계산 가능
    // - 대규모 트래픽 처리에 유리하고 스케일링에 강함
    @PostMapping("/{postId}/like/optimistic-table")
    public void likePostTable(@PathVariable  Long postId, @RequestParam Long memberId) throws SQLException {
        createPostLikeUsecase.execute(postId, memberId);
    }
}

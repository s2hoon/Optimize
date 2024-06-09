package com.example.optimize.application.controller;


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
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostWriteService postWriteService;
    private final PostReadService postReadService;
    private final GetTimelinePostsUsecase getTimelinePostsUsecase;
    private final CreatePostUsecase createPostUsecase;

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

    @PostMapping("/{postId}/like")
    public void likePost(@PathVariable  Long postId) throws SQLException {
        postWriteService.likePost(postId);
    }
    @PostMapping("/{postId}/like/optimistic")
    public void likePostOptimistic(@PathVariable  Long postId) throws SQLException {
        postWriteService.likePostOptimisticLock(postId);
    }
}

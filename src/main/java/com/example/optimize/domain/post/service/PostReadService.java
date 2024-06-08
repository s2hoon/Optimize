package com.example.optimize.domain.post.service;


import com.example.optimize.domain.post.dto.DailyPostCount;
import com.example.optimize.domain.post.dto.DailyPostCountRequest;
import com.example.optimize.domain.post.entity.Post;
import com.example.optimize.domain.post.repository.PostRepository;
import com.example.optimize.util.CursorRequest;
import com.example.optimize.util.PageCursor;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostReadService {
    private final PostRepository postRepository;

    public List<DailyPostCount> getDailyPostCounts(DailyPostCountRequest request) throws SQLException {
        return postRepository.groupByCreatedDate(request);
    }

    public Page<Post> getPost(Long memberId, PageRequest pageRequest) throws SQLException {
        return postRepository.findAllByMemberId(memberId, pageRequest);
    }

    public PageCursor<Post> getPost(Long memberId, CursorRequest cursorRequest) throws SQLException{
        var posts = findAllBy(memberId, cursorRequest);
        var nextKey = posts.stream().mapToLong(Post::getId).min().orElse(CursorRequest.NONE_KEY);
        return new PageCursor<>(cursorRequest.next(nextKey), posts);
    }
    public PageCursor<Post> getPost(List<Long> memberIds, CursorRequest cursorRequest) throws SQLException{
        var posts = findAllBy(memberIds, cursorRequest);
        var nextKey = posts.stream().mapToLong(Post::getId).min().orElse(CursorRequest.NONE_KEY);
        return new PageCursor<>(cursorRequest.next(nextKey), posts);
    }


    private List<Post> findAllBy(Long memberId, CursorRequest cursorRequest) throws SQLException {
        if (cursorRequest.hasKey()) {
            return postRepository.findAllByLessThanIdMemberIdAndOrderByIdDesc(cursorRequest.key(), memberId,
                    cursorRequest.size());
        } else {
            return postRepository.findAllByMemberIdAndOrderByIdDesc(memberId, cursorRequest.size());
        }
    }
    private List<Post> findAllBy(List<Long> memberIds, CursorRequest cursorRequest) throws SQLException {
        if (cursorRequest.hasKey()) {
            return postRepository.findAllByLessThanIdInMemberIdsAndOrderByIdDesc(cursorRequest.key(), memberIds,
                    cursorRequest.size());
        } else {
            return postRepository.findAllByInMemberIdAndOrderByIdDesc(memberIds, cursorRequest.size());
        }
    }

    public List<Post> getPost(List<Long> postIds) throws SQLException {
        return postRepository.findAllByInId(postIds);
    }
}



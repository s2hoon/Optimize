package com.example.optimize.domain.post.service;


import com.example.optimize.domain.post.dto.DailyPostCount;
import com.example.optimize.domain.post.dto.DailyPostCountRequest;
import com.example.optimize.domain.post.entity.Post;
import com.example.optimize.domain.post.repository.PostRepository;
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

}



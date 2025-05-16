package com.example.optimize.domain.post.service;

import java.sql.SQLException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.optimize.domain.post.dto.PostCommand;
import com.example.optimize.domain.post.entity.Post;
import com.example.optimize.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostWriteService {
    private final PostRepository postRepository;

    public Long create(PostCommand command) throws SQLException {
        Post post = Post.builder()
                .memberId(command.memberId())
                .contents(command.contents())
                .build();
        return postRepository.save(post).getId();
    }

    @Transactional
    public void likePost(Long postId) throws SQLException {
        var post = postRepository.findById(postId, true);
        post.incrementLikeCount();
        postRepository.save(post);
    }

    public void likePostOptimisticLock(Long postId) throws SQLException {
        var post = postRepository.findById(postId, false);
        post.incrementLikeCount();
        postRepository.save(post);
    }
}

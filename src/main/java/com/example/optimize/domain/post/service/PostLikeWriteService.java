package com.example.optimize.domain.post.service;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.example.optimize.domain.member.dto.MemberDto;
import com.example.optimize.domain.post.entity.Post;
import com.example.optimize.domain.post.entity.PostLike;
import com.example.optimize.domain.post.repository.PostLikeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostLikeWriteService {

	private final PostLikeRepository postLikeRepository;

	public Long create(Post post, MemberDto memberDto) throws SQLException {
		var postLike = PostLike.builder()
			.postId(post.getId())
			.memberId(memberDto.id())
			.build();
		return postLikeRepository.save(postLike).getId();
	}


}

package com.example.optimize.application.usecase;

import java.sql.SQLException;

import org.springframework.stereotype.Service;

import com.example.optimize.domain.member.service.MemberReadService;
import com.example.optimize.domain.post.service.PostLikeWriteService;
import com.example.optimize.domain.post.service.PostReadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePostLikeUsecase {
	private final PostReadService postReadService;
	private final MemberReadService memberReadService;
	private final PostLikeWriteService postLikeWriteService;

	public void execute(Long postId, Long memberId) throws SQLException {
		var post = postReadService.getPost(postId);
		var member = memberReadService.getMember(memberId);
		postLikeWriteService.create(post, member);

	}
}

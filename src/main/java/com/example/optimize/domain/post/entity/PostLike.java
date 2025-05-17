package com.example.optimize.domain.post.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostLike {
	private  Long id;
	private final Long memberId;
	private final Long postId;
	private final LocalDateTime createdAt;

	@Builder
	public PostLike(Long id, Long memberId, Long postId, LocalDateTime createdAt) {
		this.id = id;
		this.memberId = Objects.requireNonNull(memberId);
		this.postId = Objects.requireNonNull(postId);
		this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
	}
}

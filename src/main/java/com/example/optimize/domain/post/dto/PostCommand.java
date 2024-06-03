package com.example.optimize.domain.post.dto;

public record PostCommand(
        Long memberId,
        String contents
) {
}

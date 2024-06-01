package com.example.optimize.domain.member.entity;


import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MemberNicknameHistory {
    private Long id;
    private final Long memberId;
    //정규화 대상이냐 아니냐 고려해야하는 것은 "수정했을떄 지속해서 최신성이 필요하냐"
    private final String nickname;
    private final LocalDateTime createdAt;


    @Builder
    public MemberNicknameHistory(Long id, Long memberId, String nickname, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = Objects.requireNonNull(memberId);
        this.nickname = Objects.requireNonNull(nickname);
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
    }
}

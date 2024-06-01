package com.example.optimize.domain.follow.service;


import com.example.optimize.domain.follow.entity.Follow;
import com.example.optimize.domain.follow.repository.FollowRepository;
import com.example.optimize.domain.member.dto.MemberDto;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class FollowWriteService {
    private final FollowRepository followRepository;

    public Follow create(MemberDto fromMember, MemberDto toMember) throws SQLException {
        Assert.isTrue(!fromMember.id().equals(toMember.id()), "From, To 회원이 동일합니다.");

        Follow follow = Follow
                .builder()
                .fromMemberId(fromMember.id())
                .toMemberId(toMember.id())
                .build();
        return followRepository.save(follow);
    }
}

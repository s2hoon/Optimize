package com.example.optimize.application.usecase;

import com.example.optimize.domain.follow.entity.Follow;
import com.example.optimize.domain.follow.service.FollowReadService;
import com.example.optimize.domain.member.dto.MemberDto;
import com.example.optimize.domain.member.service.MemberReadService;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetFollowMembersUsecase {

    private final MemberReadService memberReadService;
    private final FollowReadService followReadService;

    public List<MemberDto> execute(Long memberId) throws SQLException {
        List<Follow> followings = followReadService.getFollowings(memberId);
        List<Long> followingMemberIds = followings.stream().map(Follow::getToMemberId).toList();
        return memberReadService.getMembers(followingMemberIds);
    }
}

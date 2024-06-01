package com.example.optimize.application.usecase;

import com.example.optimize.domain.follow.service.FollowWriteService;
import com.example.optimize.domain.member.dto.MemberDto;
import com.example.optimize.domain.member.service.MemberReadService;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateFollowMemberUsecase {

    private final MemberReadService memberReadService;
    private final FollowWriteService followWriteService;

    public void execute(Long fromMemberId, Long toMemberId) throws SQLException {
        MemberDto fromMember = memberReadService.getMember(fromMemberId);
        MemberDto toMember = memberReadService.getMember(toMemberId);
        followWriteService.create(fromMember, toMember);
    }

}

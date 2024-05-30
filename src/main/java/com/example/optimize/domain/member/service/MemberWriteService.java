package com.example.optimize.domain.member.service;

import com.example.optimize.domain.member.dto.MemberDto;
import com.example.optimize.domain.member.dto.RegisterMemberCommand;
import com.example.optimize.domain.member.entity.Member;
import com.example.optimize.domain.member.repository.MemberRepository;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberWriteService {

    private final MemberRepository memberRepository;
    public MemberDto create(RegisterMemberCommand registerMemberCommand) throws SQLException {
        Member member = Member.builder()
                .nickname(registerMemberCommand.nickname())
                .email(registerMemberCommand.email())
                .birthday(registerMemberCommand.birthdate())
                .build();
        return memberRepository.save(member);
    }

}

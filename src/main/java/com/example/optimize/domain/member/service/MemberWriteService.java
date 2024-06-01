package com.example.optimize.domain.member.service;

import com.example.optimize.domain.member.dto.MemberDto;
import com.example.optimize.domain.member.dto.RegisterMemberCommand;
import com.example.optimize.domain.member.entity.Member;
import com.example.optimize.domain.member.entity.MemberNicknameHistory;
import com.example.optimize.domain.member.repository.MemberNicknameHistoryRepository;
import com.example.optimize.domain.member.repository.MemberRepository;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberWriteService {

    private final MemberRepository memberRepository;
    private final MemberNicknameHistoryRepository memberNicknameHistoryRepository;
    public MemberDto create(RegisterMemberCommand registerMemberCommand) throws SQLException {
        Member member = Member.builder()
                .nickname(registerMemberCommand.nickname())
                .email(registerMemberCommand.email())
                .birthday(registerMemberCommand.birthdate())
                .build();
        return toDto(memberRepository.insert(member));
    }

    public void changeNickname(Long id, String nickname) throws SQLException {
        Member member = memberRepository.findById(id);
        member.changeNickname(nickname);
        Member savedMember = memberRepository.save(member);

        saveMemberNicknameHistory(savedMember);
    }
    private MemberDto toDto(Member member) {
        return new MemberDto(member.getId(), member.getEmail(), member.getNickname(), member.getBirthday());
    }

    private void saveMemberNicknameHistory(Member member) throws SQLException {
        MemberNicknameHistory history = MemberNicknameHistory.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();
        memberNicknameHistoryRepository.save(history);
    }
}

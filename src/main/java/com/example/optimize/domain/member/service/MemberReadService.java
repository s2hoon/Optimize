package com.example.optimize.domain.member.service;

import com.example.optimize.domain.member.dto.MemberDto;
import com.example.optimize.domain.member.entity.Member;
import com.example.optimize.domain.member.repository.MemberRepository;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberReadService {

    private final MemberRepository memberRepository;
    public MemberDto getMember(Long id) throws SQLException {
        return toDto(memberRepository.findById(id));
    }

    private MemberDto toDto(Member member) {
        return new MemberDto(member.getId(), member.getEmail(), member.getNickname(), member.getBirthday());
    }
}

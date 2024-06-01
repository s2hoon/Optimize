package com.example.optimize.domain.member.service;

import com.example.optimize.domain.member.dto.MemberDto;
import com.example.optimize.domain.member.dto.MemberNicknameHistoryDto;
import com.example.optimize.domain.member.entity.Member;
import com.example.optimize.domain.member.entity.MemberNicknameHistory;
import com.example.optimize.domain.member.repository.MemberNicknameHistoryRepository;
import com.example.optimize.domain.member.repository.MemberRepository;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberReadService {

    private final MemberRepository memberRepository;
    private final MemberNicknameHistoryRepository memberNicknameHistoryRepository;
    public MemberDto getMember(Long id) throws SQLException {
        return toDto(memberRepository.findById(id));
    }

    public List<MemberNicknameHistoryDto> getNicknameHistories(Long memberId) throws SQLException {
        var histories = memberNicknameHistoryRepository.findAllByMemberId(memberId);
        return histories.stream()
                .map(this::toDto)
                .toList();
    }
    private MemberDto toDto(Member member) {
        return new MemberDto(member.getId(), member.getEmail(), member.getNickname(), member.getBirthday());
    }
    public MemberNicknameHistoryDto toDto(MemberNicknameHistory history) {
        return new MemberNicknameHistoryDto(
                history.getId(),
                history.getMemberId(),
                history.getNickname(),
                history.getCreatedAt()
        );
    }
}

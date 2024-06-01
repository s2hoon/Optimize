package com.example.optimize.controller;


import com.example.optimize.domain.member.dto.MemberDto;
import com.example.optimize.domain.member.dto.MemberNicknameHistoryDto;
import com.example.optimize.domain.member.dto.RegisterMemberCommand;
import com.example.optimize.domain.member.service.MemberReadService;
import com.example.optimize.domain.member.service.MemberWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원정보")
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberWriteService memberWriteService;
    private final MemberReadService memberReadService;

    @Operation(summary = "회원정보 등록")
    @PostMapping("/members")
    public MemberDto register(@RequestBody RegisterMemberCommand registerMemberCommand) throws SQLException {
        return memberWriteService.create(registerMemberCommand);
    }
    @Operation(summary = "회원정보 단건 조회")
    @GetMapping("/members/{id}")
    public MemberDto getMember(@PathVariable Long id) throws SQLException {
        return memberReadService.getMember(id);
    }

    @Operation(summary = "회원이름 변경")
    @PostMapping("/members/{id}/nickname")
    public MemberDto changeNickname(@PathVariable Long id, @RequestBody String nickname) throws SQLException {
        memberWriteService.changeNickname(id, nickname);
        return memberReadService.getMember(id);
    }
    @Operation(summary = "회원이름 변경내역 조회")
    @GetMapping("/members/{id}/name-histories")
    public List<MemberNicknameHistoryDto> getMemberNameHistories(@PathVariable Long id) throws SQLException {
        return memberReadService.getNicknameHistories(id);
    }
}

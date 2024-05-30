package com.example.optimize.controller;


import com.example.optimize.domain.member.dto.MemberDto;
import com.example.optimize.domain.member.dto.RegisterMemberCommand;
import com.example.optimize.domain.member.service.MemberReadService;
import com.example.optimize.domain.member.service.MemberWriteService;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberWriteService memberWriteService;
    private final MemberReadService memberReadService;

    @PostMapping("/members")
    public MemberDto register(@RequestBody RegisterMemberCommand registerMemberCommand) throws SQLException {
        return memberWriteService.create(registerMemberCommand);
    }

    @GetMapping("/members/{id}")
    public MemberDto getMember(@PathVariable Long id) throws SQLException {
        return memberReadService.getMember(id);
    }
}

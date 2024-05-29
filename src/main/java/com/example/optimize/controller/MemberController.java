package com.example.optimize.controller;


import com.example.optimize.domain.member.dto.RegisterMemberCommand;
import com.example.optimize.domain.member.entity.Member;
import com.example.optimize.domain.member.service.MemberWriteService;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberWriteService memberWriteService;

    @PostMapping("/members")
    public Member register(@RequestBody RegisterMemberCommand registerMemberCommand) throws SQLException {
        return memberWriteService.create(registerMemberCommand);

    }



}

package com.example.optimize.domain.member.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.example.optimize.util.MemberFixtureFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemberTest {

    @Test
    @DisplayName("회원은 닉네임을 변경할 수 있다")
    void changeNickname() {
        Member member = MemberFixtureFactory.create();
        String expected = "pnu";
        member.changeNickname(expected);
        Assertions.assertEquals(expected, member.getNickname());
    }

    @DisplayName("회원 닉네임의 길이는 10자를 초과할 수 없다")
    @Test
    public void testNicknameMaxLength() {
        Member member = MemberFixtureFactory.create();
        String overMaxLengthName = "superChairman";

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> member.changeNickname(overMaxLengthName)
        );
    }
}
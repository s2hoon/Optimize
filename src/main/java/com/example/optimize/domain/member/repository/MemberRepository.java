package com.example.optimize.domain.member.repository;


import static com.example.optimize.util.DBConnectionUtil.close;
import static com.example.optimize.util.DBConnectionUtil.getConnection;

import com.example.optimize.domain.member.entity.Member;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Repository
@Slf4j
public class MemberRepository {
    public Member save(Member member) throws SQLException {
        if (member.getId() == null) {
            return insert(member);
        }
        return update(member);
    }

    public Member update(Member member) throws SQLException {
        String sql = "update member set email=?,nickname=?, birthday=? where id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getEmail());
            pstmt.setString(2, member.getNickname());
            LocalDate birthday = member.getBirthday();
            if (birthday != null) {
                pstmt.setDate(3, Date.valueOf(birthday));
            } else {
                pstmt.setDate(3, null);
            }
            pstmt.setLong(4, member.getId());
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize = {}", resultSize);
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member insert(Member member) throws SQLException{
        String sql = "insert into member(email,nickname,birthday, createdAt) values(?,?,?,?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,member.getEmail());
            pstmt.setString(2, member.getNickname());
            LocalDate birthday = member.getBirthday();
            if (birthday != null) {
                pstmt.setDate(3, Date.valueOf(birthday));
            } else {
                pstmt.setDate(3, null);
            }
            LocalDateTime createdAt = member.getCreatedAt();
            if (createdAt != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(createdAt));
            } else {
                pstmt.setTimestamp(4, null);
            }
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long generatedId = generatedKeys.getLong(1);
                member.setId(generatedId);
            }
            return member;
        } catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con, pstmt, null);
        }

    }


    public Member findById(Long id) throws SQLException {
        String sql = "select * from member where id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = Member.builder()
                        .id(rs.getLong("id"))
                        .nickname(rs.getString("nickname"))
                        .email(rs.getString("email"))
                        .birthday(rs.getTimestamp("birthday").toLocalDateTime().toLocalDate())
                        .build();
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId =" + id);
            }
        } catch (SQLException e) {//ResultSet executeQuery() throws SQLException;
            log.error("db error",e);
            throw e;
        }finally{
            close(con, pstmt, rs);
        }
    }

    public List<Member> findAllByIdIn(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        String sql = "SELECT * FROM member WHERE id IN (" + ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", ")) + ")";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i < ids.size(); i++) {
                pstmt.setLong(i + 1, ids.get(i));
            }
            rs = pstmt.executeQuery();
            List<Member> results = new ArrayList<>();
            while (rs.next()) {
                Member member = Member.builder()
                        .id(rs.getLong("id"))
                        .nickname(rs.getString("nickname"))
                        .email(rs.getString("email"))
                        .birthday(rs.getTimestamp("birthday").toLocalDateTime().toLocalDate())
                        .build();
                results.add(member);
            }
            if (results.isEmpty()) {
                throw new NoSuchElementException("No members found for the given IDs");
            }
            return results;
        } catch (SQLException e) {
            log.error("db error", e);
            throw new RuntimeException("Database error occurred", e);
        } finally {
            close(con, pstmt, rs);
        }
    }

}

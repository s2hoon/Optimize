package com.example.optimize.domain.member.repository;


import static com.example.optimize.util.DBConnectionUtil.close;
import static com.example.optimize.util.DBConnectionUtil.getConnection;

import com.example.optimize.domain.member.entity.MemberNicknameHistory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MemberNicknameHistoryRepository{
    public MemberNicknameHistory save(MemberNicknameHistory history) throws SQLException {
        if (history.getId() == null) {
            return insert(history);
        }
        throw new UnsupportedOperationException("MemberNicknameHistory 는 갱신을 지원하지 않습니다");
    }
    public MemberNicknameHistory insert(MemberNicknameHistory history) throws SQLException{
        String sql = "insert into membernicknamehistory(memberId,nickname,createdAt) values(?,?,?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1,history.getMemberId());
            pstmt.setString(2, history.getNickname());
            LocalDateTime createdAt = history.getCreatedAt();
            if (createdAt != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
            } else {
                pstmt.setTimestamp(3, null);
            }
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long generatedId = generatedKeys.getLong(1);
                history.setId(generatedId);
            }
            return history;
        } catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con, pstmt, null);
        }

    }


    public List<MemberNicknameHistory> findAllByMemberId(Long id) throws SQLException {
        String sql = "select * from membernicknamehistory where memberId = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            List<MemberNicknameHistory> results = new ArrayList<>();
            while (rs.next()) {
                MemberNicknameHistory history = MemberNicknameHistory.builder()
                        .id(rs.getLong("id"))
                        .memberId(rs.getLong("memberId"))
                        .nickname(rs.getString("nickname"))
                        .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                        .build();
                results.add(history);

            }
            if (results.isEmpty()) {
                throw new NoSuchElementException("member not found memberId =" + id);
            }
            return results;

        } catch (SQLException e) {//ResultSet executeQuery() throws SQLException;
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }


    }



}

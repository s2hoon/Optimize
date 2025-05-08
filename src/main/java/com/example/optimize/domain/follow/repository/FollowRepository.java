package com.example.optimize.domain.follow.repository;

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

import org.springframework.stereotype.Repository;

import com.example.optimize.domain.follow.entity.Follow;
import com.example.optimize.util.DBConnectionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class FollowRepository {

    private final DBConnectionUtil dbConnectionUtil;

    public Follow save(Follow follow) throws SQLException {
        if (follow.getId() == null)
            return insert(follow);

        throw new UnsupportedOperationException("Follow는 갱신을 지원하지 않습니다");
    }

    private Follow insert(Follow follow) throws SQLException {
        String sql = "insert into follow(fromMemberId,toMemberId,createdAt) values(?,?,?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dbConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, follow.getFromMemberId());
            pstmt.setLong(2, follow.getToMemberId());
            LocalDateTime createdAt = follow.getCreatedAt();
            if (createdAt != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
            } else {
                pstmt.setTimestamp(3, null);
            }
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long generatedId = generatedKeys.getLong(1);
                follow.setId(generatedId);
            }
            return follow;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            dbConnectionUtil.close(con, pstmt, null);
        }

    }

    public List<Follow> findAllByToMemberId(Long toMemberId) throws SQLException {
        String sql = "Select * from follow where toMemberId = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dbConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, toMemberId);
            rs = pstmt.executeQuery();
            List<Follow> results = new ArrayList<>();
            while (rs.next()) {
                Follow follow = Follow.builder()
                        .id(rs.getLong("id"))
                        .toMemberId(rs.getLong("toMemberId"))
                        .fromMemberId(rs.getLong("fromMemberId"))
                        .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                        .build();
                results.add(follow);
            }
            if (results.isEmpty()) {
                throw new NoSuchElementException("follow not found toMemberId =" + toMemberId);
            }
            return results;

        } catch (SQLException e) {//ResultSet executeQuery() throws SQLException;
            log.error("db error", e);
            throw e;
        } finally {
            dbConnectionUtil.close(con, pstmt, rs);
        }

    }
    public List<Follow> findAllByFromMemberId(Long fromMemberId) throws SQLException {
        String sql = "Select * from follow where fromMemberId = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dbConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, fromMemberId);
            rs = pstmt.executeQuery();
            List<Follow> results = new ArrayList<>();
            while (rs.next()) {
                Follow follow = Follow.builder()
                        .id(rs.getLong("id"))
                        .toMemberId(rs.getLong("toMemberId"))
                        .fromMemberId(rs.getLong("fromMemberId"))
                        .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                        .build();
                results.add(follow);
            }
            if (results.isEmpty()) {
                throw new NoSuchElementException("follow not found fromMemberId =" + fromMemberId);
            }
            return results;

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            dbConnectionUtil.close(con, pstmt, rs);
        }

    }





}

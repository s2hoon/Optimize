package com.example.optimize.domain.post.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.optimize.domain.post.entity.Timeline;
import com.example.optimize.util.DBConnectionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TimelineRepository {

    private final DBConnectionUtil dbConnectionUtil;


    private static final RowMapper<Timeline> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Timeline.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .postId(resultSet.getLong("postId"))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();

    public Timeline save(Timeline timeline) throws SQLException {
        if (timeline.getId() == null) {
            return insert(timeline);
        }
        throw new UnsupportedOperationException("Timeline 은 갱신을 지원하지 않습니다");

    }

    public List<Timeline> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) throws SQLException {
        String sql = String.format("SELECT * FROM timeline WHERE memberId = ? ORDER BY id DESC LIMIT ?");
        List<Timeline> timelines = new ArrayList<>();
        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, memberId);
            pstmt.setInt(2, size);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                int rowNum = 0;
                while (resultSet.next()) {
                    Timeline timeline = ROW_MAPPER.mapRow(resultSet, rowNum++);
                    timelines.add(timeline);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return timelines;
    }
    public List<Timeline> findAllByLessThanIdMemberIdAndOrderByIdDesc(Long id , Long memberId, int size) throws SQLException {
        String sql = String.format("SELECT * FROM timeline WHERE memberId = ? AND id < ? ORDER BY id DESC LIMIT ?");
        List<Timeline> timelines = new ArrayList<>();
        try (Connection conn = dbConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, memberId);
            pstmt.setLong(2, id);
            pstmt.setInt(3, size);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                int rowNum = 0;
                while (resultSet.next()) {
                    Timeline timeline = ROW_MAPPER.mapRow(resultSet, rowNum++);
                    timelines.add(timeline);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return timelines;
    }
    private Timeline insert(Timeline timeline) throws SQLException {
        String sql = "insert into timeline(memberId, postId , createdAt) values(?,?,?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try{
            con = dbConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1,timeline.getMemberId());
            pstmt.setLong(2, timeline.getMemberId());
            LocalDateTime createdAt = timeline.getCreatedAt();
            if (createdAt != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
            } else {
                pstmt.setTimestamp(3, null);
            }
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long generatedId = generatedKeys.getLong(1);
                timeline.setId(generatedId);
            }
            return timeline;
        } catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            dbConnectionUtil.close(con, pstmt, null);
        }

    }
    public void bulkInsert(List<Timeline> timelines) throws SQLException {
        String sql = """
                INSERT INTO timeline (memberId, postId, createdAt)
                VALUES (?, ?,  ?)
                """;
        try (Connection con = dbConnectionUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            for (Timeline timeline : timelines) {
                pstmt.setLong(1, timeline.getMemberId());
                pstmt.setLong(2, timeline.getPostId());
                LocalDateTime createdAt = timeline.getCreatedAt();
                if (createdAt != null) {
                    pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
                } else {
                    pstmt.setTimestamp(3, null);
                }
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

}

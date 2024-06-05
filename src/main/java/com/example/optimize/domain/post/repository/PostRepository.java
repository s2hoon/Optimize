package com.example.optimize.domain.post.repository;


import static com.example.optimize.util.DBConnectionUtil.close;
import static com.example.optimize.util.DBConnectionUtil.getConnection;

import com.example.optimize.domain.post.dto.DailyPostCount;
import com.example.optimize.domain.post.dto.DailyPostCountRequest;
import com.example.optimize.domain.post.entity.Post;
import com.example.optimize.util.DBConnectionUtil;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
@Slf4j
public class PostRepository {


    private static final RowMapper<Post> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Post.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .contents(resultSet.getString("contents"))
            .createdDate(resultSet.getObject("createdDate", LocalDate.class))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
//            .likeCount(resultSet.getLong("likeCount"))
//            .version(resultSet.getLong("version"))
            .build();

    public Post save(Post post) throws SQLException {
        return insert(post);
    }

    private Post insert(Post post) throws SQLException {
        String sql = "insert into post(memberId,contents,createdDate, createdAt) values(?,?,?,?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1,post.getMemberId());
            pstmt.setString(2, post.getContents());
            LocalDate createdDate = post.getCreatedDate();
            if (createdDate != null) {
                pstmt.setDate(3, Date.valueOf(createdDate));
            } else {
                pstmt.setDate(3, null);
            }
            LocalDateTime createdAt = post.getCreatedAt();
            if (createdAt != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(createdAt));
            } else {
                pstmt.setTimestamp(4, null);
            }
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long generatedId = generatedKeys.getLong(1);
                post.setId(generatedId);
            }
            return post;
        } catch (SQLException e){
            log.error("db error", e);
            throw e;
        }finally{
            close(con, pstmt, null);
        }

    }


    public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) throws SQLException {
        String sql = """
            SELECT memberId, createdDate, count(id) as cnt 
            FROM post 
            WHERE memberId = ? AND createdDate BETWEEN ? AND ?
            GROUP BY memberId, createdDate
            """;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, request.memberId());
            pstmt.setDate(2, Date.valueOf(request.firstDate()));
            pstmt.setDate(3, Date.valueOf(request.lastDate()));
            rs = pstmt.executeQuery();
            List<DailyPostCount> results = new ArrayList<>();
            while (rs.next()) {
                DailyPostCount dailyPostCount = new DailyPostCount(
                        rs.getLong("memberId"),
                        rs.getObject("createdDate", LocalDate.class),
                        rs.getLong("cnt")
                );
                results.add(dailyPostCount);
            }
            return results;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void bulkInsert(List<Post> posts) throws SQLException {
        String sql = """
                INSERT INTO post (memberId, contents, createdDate, createdAt)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            for (Post post : posts) {
                pstmt.setLong(1, post.getMemberId());
                pstmt.setString(2, post.getContents());

                LocalDate createdDate = post.getCreatedDate();
                if (createdDate != null) {
                    pstmt.setDate(3, Date.valueOf(createdDate));
                } else {
                    pstmt.setDate(3, null);
                }

                LocalDateTime createdAt = post.getCreatedAt();
                if (createdAt != null) {
                    pstmt.setTimestamp(4, Timestamp.valueOf(createdAt));
                } else {
                    pstmt.setTimestamp(4, null);
                }

                pstmt.addBatch();
            }

            pstmt.executeBatch();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        }
    }

    public List<Post> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) throws SQLException {
        String sql = String.format("SELECT * FROM post WHERE memberId = ? ORDER BY id DESC LIMIT ?");
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setLong(1, memberId);
             pstmt.setInt(2, size);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                int rowNum = 0;
                while (resultSet.next()) {
                    Post post = ROW_MAPPER.mapRow(resultSet, rowNum++);
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return posts;
    }


    public List<Post> findAllByLessThanIdMemberIdAndOrderByIdDesc(Long id , Long memberId, int size) throws SQLException {
        String sql = String.format("SELECT * FROM post WHERE memberId = ? AND id < ? ORDER BY id DESC LIMIT ?");
        List<Post> posts = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, memberId);
            pstmt.setLong(2, id);
            pstmt.setInt(3, size);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                int rowNum = 0;
                while (resultSet.next()) {
                    Post post = ROW_MAPPER.mapRow(resultSet, rowNum++);
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return posts;
    }

    public Page<Post> findAllByMemberId(Long memberId, PageRequest pageRequest) throws SQLException {
        int offset = pageRequest.getPageNumber() * pageRequest.getPageSize();
        int size = pageRequest.getPageSize();
        String orderBy = "createdAt";
        Sort sort = pageRequest.getSort();
        List<Post> posts = new ArrayList<>();
        String query = String.format("""
            SELECT *
            FROM post
            WHERE memberId = ?
            ORDER BY %s
            LIMIT ?
            OFFSET ?
            """, orderBy);
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, memberId);
            statement.setInt(2, size);
            statement.setInt(3, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                int rowNum = 0;
                while (resultSet.next()) {
                    Post post = ROW_MAPPER.mapRow(resultSet, rowNum++);
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return new PageImpl<>(posts, pageRequest, getCount(memberId));
    }

    private long getCount(Long memberId) throws SQLException {
        String countQuery = String.format("SELECT COUNT(*) FROM post WHERE memberId = ?");
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(countQuery)) {
            statement.setLong(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return 0;
    }


}

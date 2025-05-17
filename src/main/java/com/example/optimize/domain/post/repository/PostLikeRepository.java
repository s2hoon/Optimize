package com.example.optimize.domain.post.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.optimize.domain.post.entity.PostLike;
import com.example.optimize.util.DBConnectionUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class PostLikeRepository {

	private final DBConnectionUtil dbConnectionUtil;


	private static final RowMapper<PostLike> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> PostLike.builder()
		.id(resultSet.getLong("id"))
		.memberId(resultSet.getLong("memberId"))
		.postId(resultSet.getLong("postId"))
		.createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
		.build();

	public PostLike save(PostLike postLike) throws SQLException {
		if (postLike.getId() == null) {
			return insert(postLike);
		}
		throw new UnsupportedOperationException("postLike 은 갱신을 지원하지 않습니다");

	}



	private PostLike insert(PostLike postLike) throws SQLException {
		String sql = "insert into postlike(memberId, postId , createdAt) values(?,?,?)";
		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			con = dbConnectionUtil.getConnection();
			pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1,postLike.getMemberId());
			pstmt.setLong(2, postLike.getPostId());
			LocalDateTime createdAt = postLike.getCreatedAt();
			if (createdAt != null) {
				pstmt.setTimestamp(3, Timestamp.valueOf(createdAt));
			} else {
				pstmt.setTimestamp(3, null);
			}
			pstmt.executeUpdate();
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				Long generatedId = generatedKeys.getLong(1);
				postLike.setId(generatedId);
			}
			return postLike;
		} catch (SQLException e){
			log.error("db error", e);
			throw e;
		}finally{
			dbConnectionUtil.close(con, pstmt, null);
		}

	}

}

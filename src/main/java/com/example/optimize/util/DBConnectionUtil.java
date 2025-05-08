package com.example.optimize.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DBConnectionUtil {

    private final DataSource dataSource;

    public Connection getConnection() {
        try {
            Connection connection = dataSource.getConnection();
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException("DB 커넥션 획득 실패", e);
        }
    }

    public void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { log.warn("ResultSet close 실패", e); }
        }
        if (pstmt != null) {
            try { pstmt.close(); } catch (SQLException e) { log.warn("PreparedStatement close 실패", e); }
        }
        if (con != null) {
            try { con.close(); } catch (SQLException e) { log.warn("Connection close 실패", e); }
        }
    }
}

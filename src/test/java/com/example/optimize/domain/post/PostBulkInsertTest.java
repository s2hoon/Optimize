package com.example.optimize.domain.post;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import com.example.optimize.domain.post.entity.Post;
import com.example.optimize.domain.post.repository.PostRepository;
import com.example.optimize.util.PostFixtureFactory;

@SpringBootTest
public class PostBulkInsertTest {
    @Autowired
    private PostRepository postRepository;


    @Test
    public void bulkInsert() throws SQLException {
        var easyRandom = PostFixtureFactory.get(
                3L,
                LocalDate.of(1970, 1, 1),
                LocalDate.of(2022, 4, 1)
        );

        var stopWatch = new StopWatch();
        stopWatch.start();

        int _1만 = 10000;
        var posts = IntStream.range(0,  _1만* 100)
                .parallel()
                .mapToObj(i -> easyRandom.nextObject(Post.class))
                .toList();

        stopWatch.stop();
        System.out.println("객체 생성 시간 : " + stopWatch.getTotalTimeSeconds());

        var queryStopWatch = new StopWatch();
        queryStopWatch.start();

        postRepository.bulkInsert(posts);

        queryStopWatch.stop();
        System.out.println("DB 인서트 시간 : " + queryStopWatch.getTotalTimeSeconds());
    }


}

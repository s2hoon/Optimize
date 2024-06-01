package com.example.optimize.domain.follow.service;

import com.example.optimize.domain.follow.entity.Follow;
import com.example.optimize.domain.follow.repository.FollowRepository;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class FollowReadService {

    final private FollowRepository followRepository;

    public List<Follow> getFollowings(Long memberId) throws SQLException {
        return followRepository.findAllByFromMemberId(memberId);
    }

    public List<Follow> getFollowers(Long memberId) throws SQLException {
        return followRepository.findAllByToMemberId(memberId);
    }
}

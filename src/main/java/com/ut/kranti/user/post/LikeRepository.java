package com.ut.kranti.user.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);
}

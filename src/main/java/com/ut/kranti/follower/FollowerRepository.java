package com.ut.kranti.follower;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

	List<Follower> findByFollowingId(Long userId);

	List<Follower> findByFollowerId(Long userId);

}

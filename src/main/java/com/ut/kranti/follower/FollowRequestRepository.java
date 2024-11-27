package com.ut.kranti.follower;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ut.kranti.user.UserProfile;

@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

	 Optional<FollowRequest> findBySenderAndReceiver(UserProfile sender, UserProfile receiver);

}

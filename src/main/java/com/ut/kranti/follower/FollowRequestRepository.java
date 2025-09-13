package com.ut.kranti.follower;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ut.kranti.user.UserProfile;

@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

	 Optional<FollowRequest> findBySenderAndReceiver(UserProfile sender, UserProfile receiver);

	 Optional<FollowRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
	 @Query("SELECT new com.ut.kranti.follower.FollowRequestDTO(fr.id, s.username, r.username) " +
		       "FROM FollowRequest fr " +
		       "JOIN fr.sender s " +
		       "JOIN fr.receiver r " +
		       "WHERE fr.receiver.id = :receiverId")
		List<FollowRequestDTO> findFollowRequestsByReceiverId(@Param("receiverId") Long receiverId);


}

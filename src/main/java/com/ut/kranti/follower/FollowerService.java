package com.ut.kranti.follower;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ut.kranti.exception.ResourceNotFoundException;
import com.ut.kranti.user.UserDto;
import com.ut.kranti.user.UserMapper;
import com.ut.kranti.user.UserProfile;
import com.ut.kranti.user.UserRepository;

@Service
public class FollowerService {

    @Autowired
    private FollowRequestRepository followRequestRepository;

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private UserRepository userRepository; // Assuming UserRepository exists to fetch user profiles

    public void sendFollowRequest(Long senderId, Long receiverId) {
        UserProfile sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        UserProfile receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        FollowRequest followRequest = new FollowRequest();
        followRequest.setSender(sender);
        followRequest.setReceiver(receiver);
        followRequest.setRequestedAt(LocalDateTime.now());
        followRequest.setStatus(FollowRequest.RequestStatus.PENDING);

        followRequestRepository.save(followRequest);
    }

    public void approveFollowRequest(Long requestId) {
        FollowRequest followRequest = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Follow request not found"));

        if (followRequest.getStatus() == FollowRequest.RequestStatus.PENDING) {
            followRequest.setStatus(FollowRequest.RequestStatus.ACCEPTED);
            followRequestRepository.save(followRequest);

            // Create a follower relationship
            Follower follower = new Follower();
            follower.setFollower(followRequest.getSender());
            follower.setFollowing(followRequest.getReceiver());
            follower.setFollowDate(LocalDateTime.now());

            followerRepository.save(follower);
        }
    }

    public void rejectFollowRequest(Long requestId) {
        FollowRequest followRequest = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Follow request not found"));

        if (followRequest.getStatus() == FollowRequest.RequestStatus.PENDING) {
            followRequest.setStatus(FollowRequest.RequestStatus.REJECTED);
            followRequestRepository.save(followRequest);
        }
    }

    public List<UserDto> getFollowers(Long userId) {
       return  followerRepository.findByFollowingId(userId).stream()
    		   .map((follower->UserMapper.toDto(follower.getFollower()))).collect(Collectors.toList());
    }

    public List<UserDto> getFollowing(Long userId) {
        return followerRepository.findByFollowerId(userId).stream()
        		.map(follow->UserMapper.toDto(follow.getFollowing())).collect(Collectors.toList());
        
    }
    public void approveFollowRequest(Long userId, Long followerId) {
        FollowRequest followRequest = followRequestRepository
            .findById(followerId)
            .orElseThrow(() -> new ResourceNotFoundException("Follow request not found."));

        // Update the status to ACCEPTED
        followRequest.setStatus(FollowRequest.RequestStatus.ACCEPTED);
        followRequestRepository.save(followRequest);

        // Add the follower to the Follower table
        Follower follower = new Follower();
        follower.setFollower(followRequest.getSender());
        follower.setFollowing(followRequest.getReceiver());
        follower.setFollowDate(LocalDateTime.now());
        followerRepository.save(follower);
    }

    public void rejectFollowRequest(Long userId, Long followerId) {
        FollowRequest followRequest = followRequestRepository
            .findBySenderIdAndReceiverId(followerId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Follow request not found."));

        // Update the status to REJECTED
        followRequest.setStatus(FollowRequest.RequestStatus.REJECTED);
        followRequestRepository.save(followRequest);
    }

	public  List<FollowRequestDTO> getRequest(Long userId) {
		// TODO Auto-generated method stub
		return followRequestRepository.findFollowRequestsByReceiverId(userId);
	}
}
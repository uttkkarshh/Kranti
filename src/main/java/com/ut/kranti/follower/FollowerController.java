package com.ut.kranti.follower;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ut.kranti.user.UserDto;

@RestController
@RequestMapping("api/follow")
public class FollowerController {

    @Autowired
    private FollowerService followerService;

    @PostMapping("/request")
    public ResponseEntity<String> sendFollowRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        followerService.sendFollowRequest(senderId, receiverId);
        return ResponseEntity.ok("Follow request sent");
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<String> approveFollowRequest(@PathVariable Long requestId) {
        followerService.approveFollowRequest(requestId);
        return ResponseEntity.ok("Follow request approved");
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectFollowRequest(@PathVariable Long requestId) {
        followerService.rejectFollowRequest(requestId);
        return ResponseEntity.ok("Follow request rejected");
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followerService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserDto>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followerService.getFollowing(userId));
    }
    @GetMapping("/{userId}/request")
    public ResponseEntity<List<FollowRequestDTO>> getRequest(@PathVariable Long userId) {
    	
        return ResponseEntity.ok(followerService.getRequest(userId));
    }
    
}
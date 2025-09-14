package com.ut.kranti.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ut.kranti.follower.FollowerService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
    private  UserService userService;
	  @Autowired
	    private FollowerService followerService;
	  @PostMapping("/login")
	  public ResponseEntity<?> login(@RequestBody UserProfile user) {
	      try {
	          return ResponseEntity.ok(userService.verify(user));
	      } catch (RuntimeException ex) {
	          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	      }
	  }


    /**
     * Get user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
    	
    	Optional<UserDto> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok) // If user is present, return 200 OK with user data
                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));    }

    /**
     * Get all users.
     */
    @GetMapping("/allusers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Create a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody UserProfile userDto) {
    	
        UserDto createdUser = userService.saveUser(userDto);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Update an existing user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete a user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    
     //Search users by name.
     
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String name) {
        List<UserDto> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/user")
    public ResponseEntity<UserDto> getUser(@RequestParam String name) {
        UserProfile users = userService.findByUsername(name);
        return ResponseEntity.ok(UserMapper.toDto(users));
    }
    
     //Follow a user.
     
    @PostMapping("/{id}/follow")
    public ResponseEntity<String> followUser(@PathVariable Long id, @RequestParam Long followerId) {
    	
        userService.followUser(id, followerId);
        return ResponseEntity.ok("Requested");
    }
    @PostMapping("/followapprove")
    public ResponseEntity<String> approveFollowRequest( @RequestParam Long requestId) {
        followerService.approveFollowRequest(requestId);
        return ResponseEntity.ok("Follow request approved.");
    }

    @PostMapping("/{id}/followreject")
    public ResponseEntity<String> rejectFollowRequest(@PathVariable Long id, @RequestParam Long followerId) {
        followerService.rejectFollowRequest(id, followerId);
        return ResponseEntity.ok("Follow request rejected.");
    }
/*
    
    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<String> unfollowUser(@PathVariable Long id, @RequestParam Long followerId) {
        userService.unfollowUser(id, followerId);
        return ResponseEntity.ok("Unfollowed user successfully");
    }*/
}
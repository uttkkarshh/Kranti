package com.ut.kranti.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ut.kranti.follower.FollowerService;
import com.ut.kranti.auth.JWTService;
import com.ut.kranti.follower.FollowRequest;

import java.util.List;
import java.util.Optional;
import java.security.Principal;

// logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
    private  UserService userService;
	  @Autowired
	    private FollowerService followerService;

    @Autowired
    private JWTService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserProfile user) {
        logger.info("POST /api/users/login username={}", user != null ? user.getUsername() : null);
        return userService.verify(user);
    }

    /**
     * Get user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        logger.debug("GET /api/users/{}", id);
     	Optional<UserDto> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok) // If user is present, return 200 OK with user data
                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));    }

    /**
     * Get all users.
     */
    @GetMapping("/allusers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.debug("GET /api/users/allusers");
        List<UserDto> users = userService.getAllUsers();
        logger.info("Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Create a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody UserProfile userDto) {
        logger.info("POST /api/users/register username={}", userDto != null ? userDto.getUsername() : null);
        UserDto createdUser = userService.saveUser(userDto);
        logger.debug("User created id={}", createdUser.getId());
        return ResponseEntity.ok(createdUser);
    }

    /**
     * Update an existing user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        logger.info("PUT /api/users/{} update request", id);
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete a user.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.warn("DELETE /api/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    
     //Search users by name.
     
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String name) {
        logger.debug("GET /api/users/search name={}", name);
        List<UserDto> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/user")
    public ResponseEntity<UserDto> getUser(@RequestParam String name) {
        logger.debug("GET /api/users/user name={}", name);
        UserProfile users = userService.findByUsername(name);
        return ResponseEntity.ok(UserMapper.toDto(users));
    }
    
     //Follow a user.
     
    @PostMapping("/{id}/follow")
    public ResponseEntity<String> followUser(@PathVariable Long id, @RequestParam(required = false) Long followerId, Principal principal) {
        logger.info("POST /api/users/{}/follow followerId={}", id, followerId);
        // Prefer principal if available
        Long actorId = null;
        if (principal != null) {
            UserProfile p = userService.findByUsername(principal.getName());
            actorId = p.getId();
        } else if (followerId != null) {
            actorId = followerId;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Must be authenticated or provide followerId");
        }

        userService.followUser(id, actorId);
        return ResponseEntity.ok("Requested");
    }

    @PostMapping("/followapprove")
    public ResponseEntity<String> approveFollowRequest(@RequestParam Long requestId, Principal principal) {
        logger.info("POST /api/users/followapprove requestId={}", requestId);
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        // Ensure the authenticated user is the receiver of the follow request
        FollowRequest fr = followerService.getFollowRequest(requestId);
        if (!fr.getReceiver().getUsername().equals(principal.getName())) {
            logger.warn("User {} attempted to approve request {} for receiver {}", principal.getName(), requestId, fr.getReceiver().getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to approve this follow request");
        }

        followerService.approveFollowRequest(requestId);
        return ResponseEntity.ok("Follow request approved.");
    }

    @PostMapping("/followreject")
    public ResponseEntity<String> rejectFollowRequest(@RequestParam Long requestId, Principal principal) {
        logger.info("POST /api/users/followreject requestId={}", requestId);
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        FollowRequest fr = followerService.getFollowRequest(requestId);
        if (!fr.getReceiver().getUsername().equals(principal.getName())) {
            logger.warn("User {} attempted to reject request {} for receiver {}", principal.getName(), requestId, fr.getReceiver().getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed to reject this follow request");
        }

        followerService.rejectFollowRequest(requestId);
        return ResponseEntity.ok("Follow request rejected.");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, Principal principal) {
        logger.info("POST /api/users/logout principal={}", principal != null ? principal.getName() : null);
        // Principal must be set by security filter
        if (principal == null) {
            logger.warn("Logout attempted without authenticated principal");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Logout called without Bearer token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing Authorization header");
        }
        String token = authHeader.substring(7);

        String tokenSub = jwtService.extractUserName(token);
        if (tokenSub == null) {
            logger.warn("Logout token has no subject or failed to parse");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }

        if (!tokenSub.equals(principal.getName())) {
            logger.warn("Token subject {} does not match authenticated principal {}. Rejecting logout.", tokenSub, principal.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token does not belong to authenticated user");
        }

        Long exp = jwtService.extractExpirationEpoch(token);
        logger.info("User {} requested logout for token exp={}", principal.getName(), exp);
        return ResponseEntity.ok("Logged out");
    }

    // New endpoint: return authenticated user
    @GetMapping("/me")
    public ResponseEntity<UserDto> currentUser(Principal principal) {
        logger.debug("GET /api/users/me principal={}", principal != null ? principal.getName() : null);
        if (principal == null) {
            logger.warn("Unauthorized access to /api/users/me - no principal");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserProfile u = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(UserMapper.toDto(u));
    }

}

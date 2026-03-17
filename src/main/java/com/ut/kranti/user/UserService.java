package com.ut.kranti.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ut.kranti.auth.JWTService;
import com.ut.kranti.exception.ResourceNotFoundException;
import com.ut.kranti.follower.FollowRequest;
import com.ut.kranti.follower.FollowRequestRepository;

import jakarta.persistence.EntityNotFoundException;

// Add logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	   @Autowired
	    private JWTService jwtService;

	    @Autowired
	    AuthenticationManager authManager;
	    @Autowired
	    private UserRepository userRepository;
	    @Autowired
	    private FollowRequestRepository followRequestRepository;

	    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	   public ResponseEntity<?> verify(UserProfile user) {
	       logger.info("Login attempt for username={}", user != null ? user.getUsername() : null);
	       try {
	        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
	        if (authentication.isAuthenticated()) {
	            String token = jwtService.generateToken(user.getUsername());
	            Map<String, Object> response = new HashMap<>();
	            UserDto userName=UserMapper.toDto(findByUsername(user.getUsername()));
				response.put("user", userName);
	            response.put("token", token);
	            logger.info("Authentication successful for username={}", user.getUsername());
	            return ResponseEntity.ok(response);
	        } else {
	        	logger.warn("Authentication returned unauthenticated for username={}", user.getUsername());
	        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
	        }
	       } catch (BadCredentialsException ex) {
	           // incorrect username/password
	           logger.warn("Bad credentials for username={}: {}", user != null ? user.getUsername() : null, ex.getMessage());
	           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
	       } catch (AuthenticationException ex) {
	           // other authentication problems
	           logger.error("Authentication exception for username={}: {}", user != null ? user.getUsername() : null, ex.toString());
	           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication error: " + ex.getMessage());
	       } catch (Exception ex) {
	           // unexpected errors: return 400 with message to avoid 500; also log
	           logger.error("Unexpected error during authentication for username={}: {}", user != null ? user.getUsername() : null, ex.toString(), ex);
	           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authentication processing error");
	       }
	    }
  
   
    // Get all users
    public List<UserDto> getAllUsers() {
       
        List<UserDto> li=new ArrayList<UserDto>();
        for (UserProfile x :userRepository.findAll()) {
        	li.add(UserMapper.toDto(x));
        }
        logger.debug("Returning {} users", li.size());
        return li;
    }

    // Get user by ID
    public Optional<UserDto> getUserById(Long id) {
     logger.debug("Fetching user by id={}", id);
     return userRepository.findById(id)
                .map(UserMapper::toDto); // Use map to transform the Optional<UserProfile> to Optional<UserDto>
    }

    // Create or update a user
    public UserDto saveUser(UserProfile user) {
    	 user.setPassword(encoder.encode(user.getPassword()));
        UserDto dto = UserMapper.toDto(userRepository.save(user));
        logger.info("Saved user id={}, username={}", dto.getId(), dto.getName());
        return dto;
    }

    // Delete user by ID
    public void deleteUser(Long id) {
        logger.info("Deleting user id={}", id);
        userRepository.deleteById(id);
    }

 
	public UserDto updateUser(Long id, UserDto userDto) {
		logger.debug("Updating user id={}", id);
		return userRepository.findById(id)
	            .map(user -> {
	                user.setUsername(userDto.getName());
	                user.setEmail(userDto.getEmail());
	                // Update other fields as necessary
	                return UserMapper.toDto(userRepository.save(user));
	            })
	            .orElseThrow(() -> new EntityNotFoundException("User not found"));
	}

	   public UserProfile findByUsername(String username) {
	       logger.debug("Looking up user by username={}", username);
	        return userRepository.findByUsername(username)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
	    }


public List<UserDto> searchUsersByName(String name) {
	// TODO Auto-generated method stub
	List<UserProfile>users =userRepository.findByUsernameContainingIgnoreCase(name);
	logger.debug("searchUsersByName name={} found={}", name, users.size());
	return users.stream() .map(user -> UserMapper.toDto(user)) .collect(Collectors.toList());
}


public void followUser(Long userId, Long followerId) {
	logger.info("Follow request: userId={}, followerId={}", userId, followerId);

	  UserProfile userToFollow = userRepository.findById(userId)
              .orElseThrow(() -> new IllegalArgumentException("User to follow not found"));
      UserProfile follower = userRepository.findById(followerId)
              .orElseThrow(() -> new IllegalArgumentException("Follower not found"));

      // Step 2: Check if follow request already exists
      Optional<FollowRequest> existingRequest = followRequestRepository
              .findBySenderAndReceiver(follower, userToFollow);
      
      if (existingRequest.isPresent()) {
          FollowRequest request = existingRequest.get();
          if (request.getStatus() == FollowRequest.RequestStatus.PENDING) {
              throw new IllegalArgumentException("Follow request already pending");
          } else if (request.getStatus() == FollowRequest.RequestStatus.ACCEPTED) {
              throw new IllegalArgumentException("You are already following this user");
          }
      }

      // Step 3: Create a new follow request
      FollowRequest followRequest = new FollowRequest();
      followRequest.setSender(follower);
      followRequest.setReceiver(userToFollow);
      followRequest.setStatus(FollowRequest.RequestStatus.PENDING);
      followRequest.setRequestedAt(LocalDateTime.now());
      
      followRequestRepository.save(followRequest);
      logger.info("Follow request saved for userId={}, followerId={}", userId, followerId);
  }




}
package com.ut.kranti.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ut.kranti.auth.JWTService;
import com.ut.kranti.follower.FollowRequest;
import com.ut.kranti.follower.FollowRequestRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	   @Autowired
	    private JWTService jwtService;

	    @Autowired
	    AuthenticationManager authManager;
	    @Autowired
	    private UserRepository userRepository;
	    @Autowired
	    private FollowRequestRepository followRequestRepository;

	    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	   public String verify(UserProfile user) {
	        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
	        if (authentication.isAuthenticated()) {
	            return jwtService.generateToken(user.getUsername());
	        } else {
	            return "fail";
	        }
	    }
  
   
    // Get all users
    public List<UserDto> getAllUsers() {
       
        List<UserDto> li=new ArrayList<UserDto>();
        for (UserProfile x :userRepository.findAll()) {
        	li.add(UserMapper.toDto(x));
        }
        return li;
    }

    // Get user by ID
    public Optional<UserDto> getUserById(Long id) {
    	return userRepository.findById(id)
                .map(UserMapper::toDto); // Use map to transform the Optional<UserProfile> to Optional<UserDto>
    }

    // Create or update a user
    public UserDto saveUser(UserProfile user) {
    	 user.setPassword(encoder.encode(user.getPassword()));
        return UserMapper.toDto(userRepository.save(user));
    }

    // Delete user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

 
	public UserDto updateUser(Long id, UserDto userDto) {
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
	
Optional<UserProfile> user =userRepository.findByUsername(username);
if(user.isPresent()) {
	return user.get();
}
else {
	return null;
}
	
}


public List<UserDto> searchUsersByName(String name) {
	// TODO Auto-generated method stub
	List<UserProfile>users =userRepository.findByUsernameContainingIgnoreCase(name);
	return users.stream() .map(user -> UserMapper.toDto(user)) .collect(Collectors.toList());
}


public void followUser(Long userId, Long followerId) {

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
  }



}

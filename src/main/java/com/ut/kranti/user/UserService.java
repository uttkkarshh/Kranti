package com.ut.kranti.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get all users
    public List<UserProfile> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<UserProfile> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Create or update a user
    public UserProfile saveUser(UserProfile user) {
        return userRepository.save(user);
    }

    // Delete user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Search users by name
    public List<UserProfile> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }
}

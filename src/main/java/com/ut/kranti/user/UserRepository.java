package com.ut.kranti.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, Long> {
	Optional<UserProfile> findByUsername(String username); // Add this method
	List<UserProfile> findByUsernameContainingIgnoreCase(String name);
}
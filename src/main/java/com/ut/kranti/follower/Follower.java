package com.ut.kranti.follower;

import java.time.LocalDateTime;

import com.ut.kranti.user.UserProfile;


import jakarta.persistence.Entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private UserProfile follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private UserProfile following;

    private LocalDateTime followDate;
    
   
  
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserProfile getFollower() {
		return follower;
	}

	public void setFollower(UserProfile follower) {
		this.follower = follower;
	}

	public UserProfile getFollowing() {
		return following;
	}

	public void setFollowing(UserProfile following) {
		this.following = following;
	}

	public LocalDateTime getFollowDate() {
		return followDate;
	}

	public void setFollowDate(LocalDateTime followDate) {
		this.followDate = followDate;
	}

	
	
}

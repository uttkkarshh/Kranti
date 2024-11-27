package com.ut.kranti.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ut.kranti.user.UserProfile;
import com.ut.kranti.user.post.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
@Entity
public class Event {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String title;
	    private String description;
	    private LocalDateTime startDate;
	    private LocalDateTime endDate;

	    @ManyToMany(mappedBy = "hostedEvents")
	    private Set<UserProfile> hosts; // Users hosting this event

	    @ManyToMany
	    @JoinTable(
	        name = "event_followers",
	        joinColumns = @JoinColumn(name = "event_id"),
	        inverseJoinColumns = @JoinColumn(name = "user_id")
	    )
	    private Set<UserProfile> followers; // Users following this event

	    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
	    private List<Post> posts;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public LocalDateTime getStartDate() {
			return startDate;
		}

		public void setStartDate(LocalDateTime startDate) {
			this.startDate = startDate;
		}

		public LocalDateTime getEndDate() {
			return endDate;
		}

		public void setEndDate(LocalDateTime endDate) {
			this.endDate = endDate;
		}

		public Set<UserProfile> getHosts() {
			return hosts;
		}

		public void setHosts(Set<UserProfile> hosts) {
			this.hosts = hosts;
		}

		public Set<UserProfile> getFollowers() {
			return followers;
		}

		public void setFollowers(Set<UserProfile> followers) {
			this.followers = followers;
		}

		public List<Post> getPosts() {
			return posts;
		}

		public void setPosts(List<Post> posts) {
			this.posts = posts;
		}
}

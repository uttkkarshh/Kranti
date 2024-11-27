package com.ut.kranti.user.post;

import java.time.LocalDateTime;
import java.util.List;

import com.ut.kranti.comments.Comment;
import com.ut.kranti.event.Event;
import com.ut.kranti.user.UserProfile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Post {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false, length = 255)
	    private String title;

	    @Column(columnDefinition = "TEXT", nullable = false)
	    private String content;

	    @ManyToOne
	    @JoinColumn(name = "author_id", nullable = false)
	    private UserProfile author; // The user who created the post

	    @ManyToOne
	    @JoinColumn(name = "event_id", nullable = true)
	    private Event event; // The event this post belongs to (optional)

	    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Comment> comments; // Comments on this post

	    @Column(nullable = false)
	    private LocalDateTime createdAt;

	    @Column(nullable = false)
	    private LocalDateTime updatedAt;

	    // Additional metadata fields
	    private int likesCount;
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
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public UserProfile getAuthor() {
			return author;
		}
		public void setAuthor(UserProfile author) {
			this.author = author;
		}
		public Event getEvent() {
			return event;
		}
		public void setEvent(Event event) {
			this.event = event;
		}
		public List<Comment> getComments() {
			return comments;
		}
		public void setComments(List<Comment> comments) {
			this.comments = comments;
		}
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}
		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}
		public int getLikesCount() {
			return likesCount;
		}
		public void setLikesCount(int likesCount) {
			this.likesCount = likesCount;
		}
		public int getSharesCount() {
			return sharesCount;
		}
		public void setSharesCount(int sharesCount) {
			this.sharesCount = sharesCount;
		}
		private int sharesCount;

}
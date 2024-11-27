package com.ut.kranti.user.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ut.kranti.comments.Comment;
import com.ut.kranti.comments.CommentRepository;
import com.ut.kranti.exception.ResourceNotFoundException;
import com.ut.kranti.user.UserProfile;
import com.ut.kranti.user.UserRepository;
@Service
public class PostService {

	@Autowired
	PostRepository postRepository;
	  @Autowired
	    CommentRepository commentRepository;

	    @Autowired
	    UserRepository userRepository;

	public Post createPost(PostDto postDto) {
		UserProfile user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + postDto.getUserId()));
        
        Post post = new Post();
        post.setContent(postDto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(user);
        post.setLikesCount(0);
        post.setSharesCount(0);

        return postRepository.save(post);
	}

	public Post getPostById(Long postId) {
		  return postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
	}

	

	public Post updatePost(Long postId, PostDto postDto) {
		  Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        post.setContent(postDto.getContent());
	        post.setUpdatedAt(LocalDateTime.now());

	        return postRepository.save(post);
	}

	public List<Post> getAllPosts() {
		return postRepository.findAll();
	}

	public void deletePost(Long postId) {
		  Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        postRepository.delete(post);
		
	}

	public void likePost(Long postId) {
		Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        post.setLikesCount(post.getLikesCount() + 1);
        postRepository.save(post);
		
	}

	public void unlikePost(Long postId) {
	
		  Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        if (post.getLikesCount() > 0) {
	            post.setLikesCount(post.getLikesCount() - 1);
	            postRepository.save(post);
	        }
	}

	public List<Comment> getCommentsForPost(Long postId) {
		  Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        return commentRepository.findByPost(post);
	}

	public List<Post> getPostsByUser(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Post> getFeed() {
		// TODO Auto-generated method stub
		return null;
	}

	public void sharePost(Long postId) {
		// TODO Auto-generated method stub
		
	}

	

	public Comment commentOnPost(Long postId, Comment commentDto) {
		// TODO Auto-generated method stub
		return null;
	}

	

}

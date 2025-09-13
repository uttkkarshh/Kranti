package com.ut.kranti.user.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	public PostDto createPost(PostDto postDto) {
		UserProfile user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + postDto.getUserId()));
		
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setAuthor(user);
        post.setLikesCount(0);
        post.setSharesCount(0);

        return PostMapper.toDto(postRepository.save(post));
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

	public List<PostDto> getAllPosts() {
	 List<Post> posts = postRepository.findAll();
	 return posts.stream()
             .map(post -> PostMapper.toDto(post)) // Using the PostMapper to convert each Post
             .collect(Collectors.toList()); // Collect the results in a List<PostDto>
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

	public List<PostDto> getPostsByUser(Long userId) {
		 UserProfile user = userRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

	        return postRepository.findByAuthor(user).stream().map(post -> PostMapper.toDto(post)) // Using the PostMapper to convert each Post
	                .collect(Collectors.toList()); // Collect the results in a List<PostDto>
	}

	public List<Post> getFeed() {
		return postRepository.findAllByOrderByCreatedAtDesc();
	}

	public void sharePost(Long postId) {
		  Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        post.setSharesCount(post.getSharesCount() + 1);
	        postRepository.save(post);
	}

	
	public List<PostDto> getPostsFromFollowedUsers(Long userId) {
	    // Retrieve the logged-in user's profile
	    UserProfile user = userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
	    
	    // Get the list of users the logged-in user is following
	    List<UserProfile> followedUsers = user.getFollowing().stream().map(follower->follower.getFollower()).collect(Collectors.toList());

	    // Retrieve posts from all followed users
	    List<Post> post = postRepository.findByAuthorIn(followedUsers);
	    return post.stream()
	             .map(pos -> PostMapper.toDto(pos)) // Using the PostMapper to convert each Post
	             .collect(Collectors.toList()); // Collect the results in a List<PostDto>
	}
	public Comment commentOnPost(Long postId, Comment commentDto) {
		 Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        Comment comment = new Comment();
	        comment.setPost(post);
	        comment.setContent(commentDto.getContent());
	        comment.setCreatedAt(LocalDateTime.now());

	        return commentRepository.save(comment);
	}

	
	public Page<PostDto> getPostsByUser(Long userId, Pageable pageable) {
	    Page<Post> posts = postRepository.findByAuthorId(userId, pageable);
	    return PostMapper.toDtoPage(posts);
	}


}

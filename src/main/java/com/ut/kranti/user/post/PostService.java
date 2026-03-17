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
import org.springframework.transaction.annotation.Transactional;

import com.ut.kranti.comments.Comment;
import com.ut.kranti.comments.CommentRepository;
import com.ut.kranti.exception.ResourceNotFoundException;
import com.ut.kranti.user.UserProfile;
import com.ut.kranti.user.UserRepository;

// logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PostService {

	private static final Logger logger = LoggerFactory.getLogger(PostService.class);

	@Autowired
	PostRepository postRepository;
	  @Autowired
	    CommentRepository commentRepository;

	    @Autowired
	    UserRepository userRepository;

	    @Autowired
	    LikeRepository likeRepository;

	public PostDto createPost(PostDto postDto) {
		logger.debug("createPost called for userId={}", postDto.getUserId());
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

        PostDto dto = PostMapper.toDto(postRepository.save(post));
        logger.info("Post created id={} by userId={}", dto.getId(), postDto.getUserId());
        return dto;
	}

	public Post getPostById(Long postId) {
		logger.debug("getPostById called for postId={}", postId);
		  return postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));
	}

	

	public Post updatePost(Long postId, PostDto postDto) {
		logger.debug("updatePost called postId={}", postId);
		  Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        post.setContent(postDto.getContent());
	        post.setUpdatedAt(LocalDateTime.now());

	        Post updated = postRepository.save(post);
	        logger.info("Post updated id={}", postId);
	        return updated;
	}

	public List<PostDto> getAllPosts() {
	 List<Post> posts = postRepository.findAll();
	 return posts.stream()
             .map(post -> PostMapper.toDto(post)) // Using the PostMapper to convert each Post
             .collect(Collectors.toList()); // Collect the results in a List<PostDto>
	}

	public void deletePost(Long postId) {
		logger.debug("deletePost called postId={}", postId);
		  Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        postRepository.delete(post);
	        logger.info("Post deleted id={}", postId);
		
	}

	@Transactional
	public LikeResponse likePost(Long postId, Long userId) {
		logger.debug("likePost called postId={} userId={}", postId, userId);
		Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if a like by this user already exists
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            // already liked; return response indicating no change
            logger.debug("User {} already liked post {}", userId, postId);
            return new LikeResponse(false, post.getLikesCount(), "Already liked");
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);

        // update cached counter
        long count = likeRepository.countByPostId(postId);
        post.setLikesCount((int) count);
        postRepository.save(post);

        logger.info("Post {} liked by user {} now likesCount={}", postId, userId, count);
        return new LikeResponse(true, (int) count, "Liked");
	}

	@Transactional
	public LikeResponse unlikePost(Long postId, Long userId) {
		logger.debug("unlikePost called postId={} userId={}", postId, userId);
		Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Find existing like
        java.util.Optional<Like> existing = likeRepository.findByPostIdAndUserId(postId, userId);
        if (!existing.isPresent()) {
            logger.debug("User {} had not liked post {}", userId, postId);
            return new LikeResponse(false, post.getLikesCount(), "Not previously liked");
        }

        likeRepository.delete(existing.get());
        long count = likeRepository.countByPostId(postId);
        post.setLikesCount((int) count);
        postRepository.save(post);

        logger.info("Post {} unliked by user {} now likesCount={}", postId, userId, count);
        return new LikeResponse(true, (int) count, "Unliked");
	}

	public List<Comment> getCommentsForPost(Long postId) {
		logger.debug("getCommentsForPost postId={}", postId);
		  Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        return commentRepository.findByPost(post);
	}

	public List<PostDto> getPostsByUser(Long userId) {
		logger.debug("getPostsByUser userId={}", userId);
		 UserProfile user = userRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

	        return postRepository.findByAuthor(user).stream().map(post -> PostMapper.toDto(post)) // Using the PostMapper to convert each Post
	                .collect(Collectors.toList()); // Collect the results in a List<PostDto>
	}

	public List<Post> getFeed() {
		logger.debug("getFeed called");
		return postRepository.findAllByOrderByCreatedAtDesc();
	}

	public void sharePost(Long postId) {
		logger.debug("sharePost called postId={}", postId);
		  Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        post.setSharesCount(post.getSharesCount() + 1);
	        postRepository.save(post);
	        logger.info("Post {} shared, sharesCount={}", postId, post.getSharesCount());
	}

	
	public List<PostDto> getPostsFromFollowedUsers(Long userId) {
	    // Retrieve the logged-in user's profile
		logger.debug("getPostsFromFollowedUsers userId={}", userId);
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
		logger.debug("commentOnPost postId={}", postId);
		 Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

	        Comment comment = new Comment();
	        comment.setPost(post);
	        comment.setContent(commentDto.getContent());
	        comment.setCreatedAt(LocalDateTime.now());

	        Comment saved = commentRepository.save(comment);
	        logger.info("Comment {} created on post {}", saved.getId(), postId);
	        return saved;
	}

	
	public Page<PostDto> getPostsByUser(Long userId, Pageable pageable) {
		logger.debug("getPostsByUser (paged) userId={} page={}", userId, pageable.getPageNumber());
	    Page<Post> posts = postRepository.findByAuthorId(userId, pageable);
	    return PostMapper.toDtoPage(posts);
	}


}
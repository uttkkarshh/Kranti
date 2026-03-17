package com.ut.kranti.user.post;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ut.kranti.comments.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

// logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private  PostService postService;

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    // 1. Create a Post
    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        logger.debug("createPost called by userId={}", postDto.getUserId());
        PostDto createdPost = postService.createPost(postDto);
        logger.info("Post created id={} by userId={}", createdPost.getId(), postDto.getUserId());
        return ResponseEntity.ok(createdPost);
    }

    // 2. Get a Post by ID
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        logger.debug("getPostById called for postId={}", postId);
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }
   
    // 3. Get All Posts
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
    // 3. Get All Posts
    @GetMapping("/postforuser/{id}")
    public ResponseEntity<List<PostDto>> getPostsForUser(@PathVariable Long id) {
        List<PostDto> posts = postService.getPostsFromFollowedUsers(id);
        return ResponseEntity.ok(posts);
    }
    // 4. Update a Post
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody PostDto postDto) {
        Post updatedPost = postService.updatePost(postId, postDto);
        return ResponseEntity.ok(updatedPost);
    }

    // 5. Delete a Post
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    // 6. Like a Post
    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeResponse> likePost(@PathVariable Long postId, @RequestParam Long userId) {
        logger.debug("likePost called postId={} by userId={}", postId, userId);
        LikeResponse response = postService.likePost(postId, userId);
        logger.info("likePost result postId={} by userId={} success={} likesCount={}", postId, userId, response.isSuccess(), response.getLikesCount());
        return ResponseEntity.ok(response);
    }

    // 7. Unlike a Post
    @PostMapping("/{postId}/unlike")
    public ResponseEntity<LikeResponse> unlikePost(@PathVariable Long postId, @RequestParam Long userId) {
        logger.debug("unlikePost called postId={} by userId={}", postId, userId);
        LikeResponse response = postService.unlikePost(postId, userId);
        logger.info("unlikePost result postId={} by userId={} success={} likesCount={}", postId, userId, response.isSuccess(), response.getLikesCount());
        return ResponseEntity.ok(response);
    }

    // 8. Comment on a Post
    @PostMapping("/{postId}/comment")
    public ResponseEntity<Comment> commentOnPost(@PathVariable Long postId, @RequestBody Comment commentDto) {
        Comment createdComment = postService.commentOnPost(postId, commentDto);
        return ResponseEntity.ok(createdComment);
    }

    // 9. Get Comments for a Post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = postService.getCommentsForPost(postId);
        return ResponseEntity.ok(comments);
    }

    // 10. Share a Post
    @PostMapping("/{postId}/share")
    public ResponseEntity<String> sharePost(@PathVariable Long postId) {
        postService.sharePost(postId);
        return ResponseEntity.ok("Post shared");
    }

    // 11. Get Posts by User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Long userId) {
        List<PostDto> userPosts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(userPosts);
    }

    // 12. Get Posts for Feed
    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed() {
        List<Post> feedPosts = postService.getFeed();
        return ResponseEntity.ok(feedPosts);
    }
    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<Page<PostDto>> getPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PostDto> userPosts = postService.getPostsByUser(userId, pageable);
        return ResponseEntity.ok(userPosts);
    }


}
package com.ut.kranti.user.post;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ut.kranti.comments.Comment;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private  PostService postService;

    

    // 1. Create a Post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostDto postDto) {
        Post createdPost = postService.createPost(postDto);
        return ResponseEntity.ok(createdPost);
    }

    // 2. Get a Post by ID
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    // 3. Get All Posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
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
    public ResponseEntity<String> likePost(@PathVariable Long postId) {
        postService.likePost(postId);
        return ResponseEntity.ok("Post liked");
    }

    // 7. Unlike a Post
    @PostMapping("/{postId}/unlike")
    public ResponseEntity<String> unlikePost(@PathVariable Long postId) {
        postService.unlikePost(postId);
        return ResponseEntity.ok("Post unliked");
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
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
        List<Post> userPosts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(userPosts);
    }

    // 12. Get Posts for Feed
    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed() {
        List<Post> feedPosts = postService.getFeed();
        return ResponseEntity.ok(feedPosts);
    }
}

package com.ut.kranti.comments;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

    @RestController
	@RequestMapping("api/comments")
	public class CommmentController {

	    @Autowired
	    private CommentService commentService;

	    /**
	     * Create a new comment.
	     *
	     * @param commentDto The DTO containing comment data.
	     * @return The created comment.
	     */
	    @PostMapping
	    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
	        CommentDto createdComment = commentService.createComment(commentDto);
	        return ResponseEntity.ok(createdComment);
	    }

	    /**
	     * Get all comments for a specific post.
	     *
	     * @param postId The ID of the post.
	     * @return A list of comments for the specified post.
	     */
	    @GetMapping("/post/{postId}")
	    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable Long postId) {
	    	System.out.println("req");
	        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
	        return ResponseEntity.ok(comments);
	    }

	    /**
	     * Get all comments for a specific user.
	     *
	     * @param userId The ID of the user.
	     * @return A list of comments created by the user.
	     
	    @GetMapping("/user/{userId}")
	    public ResponseEntity<List<CommentDto>> getCommentsByUserId(@PathVariable Long userId) {
	        List<CommentDto> comments = commentService.getCommentsByUserId(userId);
	        return ResponseEntity.ok(comments);
	    }
*/
	    /**
	     * Update a comment by its ID.
	     *
	     * @param commentId The ID of the comment to update.
	     * @param updatedCommentDto The updated comment data.
	     * @return The updated comment.
	     */
	    @PutMapping("/{commentId}")
	    public ResponseEntity<CommentDto> updateComment(
	        @PathVariable Long commentId,
	        @RequestBody CommentDto updatedCommentDto
	    ) {
	        CommentDto updatedComment = commentService.updateComment(commentId, updatedCommentDto);
	        return ResponseEntity.ok(updatedComment);
	    }

	    /**
	     * Delete a comment by its ID.
	     *
	     * @param commentId The ID of the comment to delete.
	     * @return A response indicating success or failure.
	     */
	    @DeleteMapping("/{commentId}")
	    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
	        commentService.deleteComment(commentId);
	        return ResponseEntity.ok("Comment deleted successfully");
	    }
	}


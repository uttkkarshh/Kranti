package com.ut.kranti.comments;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ut.kranti.user.UserProfile;
import com.ut.kranti.user.UserRepository;
import com.ut.kranti.user.post.Post;
import com.ut.kranti.user.post.PostRepository;

import jakarta.persistence.EntityNotFoundException;
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new comment
    public CommentDto createComment(CommentDto commentDto) {
        // Find the associated post
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + commentDto.getPostId()));

        // Find the author of the comment
        UserProfile author = userRepository.findById(commentDto.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + commentDto.getAuthorId()));

        // Map DTO to Entity
        Comment comment = CommentMapper.toEntity(commentDto, post, author);

        // Save the comment
        Comment savedComment = commentRepository.save(comment);

        // Convert the saved comment to DTO and return
        return CommentMapper.toDto(savedComment);
    }

    // Get comments by Post ID
    public List<CommentDto> getCommentsByPostId(Long postId) {
        // Find all comments for the given post ID
        List<Comment> comments = commentRepository.findByPostId(postId);

        // Map the list of comments to DTOs
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    // Update an existing comment
    public CommentDto updateComment(Long commentId, CommentDto updatedCommentDto) {
        // Find the existing comment
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        // Update the content
        existingComment.setContent(updatedCommentDto.getContent());
        existingComment.setUpdatedAt(LocalDateTime.now());

        // Save the updated comment
        Comment updatedComment = commentRepository.save(existingComment);

        // Convert the updated comment to DTO and return
        return CommentMapper.toDto(updatedComment);
    }

    // Delete a comment by ID
    public void deleteComment(Long commentId) {
        // Find the comment
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        // Delete the comment
        commentRepository.delete(comment);
    }
}




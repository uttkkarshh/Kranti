package com.ut.kranti.comments;

import java.time.LocalDateTime;

import com.ut.kranti.user.UserProfile;
import com.ut.kranti.user.post.Post;

public class CommentMapper {

    // Convert Entity to DTO
    public static CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setAuthorName(comment.getAuthor().getUsername()); // Assuming `UserProfile` has `username`
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }

    // Convert DTO to Entity
    public static Comment toEntity(CommentDto dto, Post post, UserProfile author) {
        Comment comment = new Comment();
        comment.setPost(post); // Use the actual Post entity
        comment.setAuthor(author); // Use the actual UserProfile entity
        comment.setContent(dto.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return comment;
    }
}


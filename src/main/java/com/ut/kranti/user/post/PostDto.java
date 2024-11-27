package com.ut.kranti.user.post;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PostDto {

    @NotBlank(message = "Post content cannot be blank")
    private String content;

    private String imageUrl; // Optional field for posts with images

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    // Getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

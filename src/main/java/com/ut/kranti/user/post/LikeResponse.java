package com.ut.kranti.user.post;

public class LikeResponse {
    private boolean success;
    private int likesCount;
    private String message;

    public LikeResponse() {}

    public LikeResponse(boolean success, int likesCount, String message) {
        this.success = success;
        this.likesCount = likesCount;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
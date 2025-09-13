package com.ut.kranti.follower;

public class FollowRequestDTO {
    private Long id;
    private String senderName;
    private String receiverName;

    public FollowRequestDTO(Long id, String senderName, String receiverName) {
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

    // Getters and setters
}

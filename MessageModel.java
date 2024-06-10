package com.example.comeback;

import com.google.firebase.Timestamp;

public class MessageModel {
    private String message;
    private String senderId;
    private String imageUri;
    private String currentUserId;

    public MessageModel(String message, String senderId, String currentUserId,String imageUri) {
        this.message = message;
        this.senderId = senderId;
        this.currentUserId=currentUserId;
        this.imageUri=imageUri;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }
    public String getCurrentUserId() {
        return currentUserId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}

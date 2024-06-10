package com.example.comeback;

public class SearchModelClass {
    private String username;
    private String userId;

    public SearchModelClass(String username,String userId) {
        this.username = username;

        this.userId=userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

}

package org.zezutom.capstone.android.model;

public class UserInfo {

    private String name;

    private String email;

    private String imageUrl;

    public UserInfo(String name, String email, String imageUrl) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

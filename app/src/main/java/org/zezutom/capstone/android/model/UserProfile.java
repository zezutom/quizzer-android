package org.zezutom.capstone.android.model;

public class UserProfile {

    private String fullName;

    private String email;

    private String imageUrl;

    public UserProfile(String fullName, String email, String imageUrl) {
        this.fullName = fullName;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

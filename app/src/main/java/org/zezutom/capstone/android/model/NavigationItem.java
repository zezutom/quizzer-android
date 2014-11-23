package org.zezutom.capstone.android.model;

public class NavigationItem {

    private Integer id;

    private String title;

    private Integer imageId;

    private String imageUrl;

    public NavigationItem(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public NavigationItem(Integer id, String title, Integer imageId) {
        this.id = id;
        this.title = title;
        this.imageId = imageId;
    }

    public NavigationItem(Integer id, String title, String imageUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getImageId() {
        return imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

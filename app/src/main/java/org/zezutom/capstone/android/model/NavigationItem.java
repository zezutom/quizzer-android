package org.zezutom.capstone.android.model;

public class NavigationItem {

    private Integer id;

    private String title;

    private Integer imageId;

    public NavigationItem(Integer id, String title, Integer imageId) {
        this.id = id;
        this.title = title;
        this.imageId = imageId;
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
}

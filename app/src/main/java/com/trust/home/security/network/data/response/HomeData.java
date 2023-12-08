package com.trust.home.security.network.data.response;

public class HomeData {
    private String name;
    private String createdAt;
    private String location;

    public HomeData(String name) {
        this.name = name;
    }

    public HomeData(String name, String createdAt, String location) {
        this.name = name;
        this.createdAt = createdAt;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

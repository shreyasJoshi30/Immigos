package com.example.welcomebot;

import android.location.Location;

public class LocationBean {

    private String name;
    private String description;
    private String classify;
    private String address;
    private Location location;
    private int postcode;
    private String type;

    public LocationBean() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocationBean(String name, String description, String classify, String address, Location location, int postcode, String type) {
        this.name = name;
        this.description = description;
        this.classify = classify;
        this.address = address;
        this.location = location;
        this.postcode = postcode;
        this.type = type;
    }



}

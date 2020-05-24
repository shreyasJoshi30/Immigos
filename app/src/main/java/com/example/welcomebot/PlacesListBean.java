package com.example.welcomebot;

import android.location.Location;

/**
 * PoJo class with getters and setters to display places from google places API
 */
public class PlacesListBean {

    private String placeId;
    private String name;
    private String address;
    private Location location;
    private  Double rating;
    private Boolean openNow;
    private Float distance;
    private String total_user_ratings;



    public PlacesListBean() {

    }

    public PlacesListBean(String placeId, String name, String address, Location location, Double rating, Boolean openNow, Float distance, String total_user_ratings) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.location = location;
        this.rating = rating;
        this.openNow = openNow;
        this.distance = distance;
        this.total_user_ratings = total_user_ratings;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getTotal_user_ratings() {
        return total_user_ratings;
    }

    public void setTotal_user_ratings(String total_user_ratings) {
        this.total_user_ratings = total_user_ratings;
    }
}

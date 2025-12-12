package com.example.restaurantapps.data.remote.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * DATA LAYER: API Response Model
 * 
 * Model ini hanya untuk mapping JSON dari API.
 * Berbeda dengan domain Restaurant - ini spesifik untuk API.
 */
public class RestaurantResponse {
    @SerializedName("error")
    private boolean error;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("count")
    private int count;
    
    @SerializedName("restaurants")
    private List<RestaurantDto> restaurants;

    public RestaurantResponse() {
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public List<RestaurantDto> getRestaurants() {
        return restaurants;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRestaurants(List<RestaurantDto> restaurants) {
        this.restaurants = restaurants;
    }
}

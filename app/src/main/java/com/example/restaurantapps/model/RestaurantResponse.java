package com.example.restaurantapps.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * STEP 2: MODEL CLASS - RestaurantResponse
 * 
 * Class ini merepresentasikan struktur keseluruhan response dari API.
 * Berisi informasi:
 * - error: status error dari API
 * - message: pesan dari server
 * - count: jumlah restaurant yang dikembalikan
 * - restaurants: List berisi banyak object Restaurant
 * 
 * Gson akan otomatis convert JSON response menjadi object RestaurantResponse ini.
 */
public class RestaurantResponse {
    
    @SerializedName("error")
    private boolean error;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("count")
    private int count;
    
    @SerializedName("restaurants")
    private List<Restaurant> restaurants;

    public RestaurantResponse() {
    }

    public RestaurantResponse(boolean error, String message, int count, List<Restaurant> restaurants) {
        this.error = error;
        this.message = message;
        this.count = count;
        this.restaurants = restaurants;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
}

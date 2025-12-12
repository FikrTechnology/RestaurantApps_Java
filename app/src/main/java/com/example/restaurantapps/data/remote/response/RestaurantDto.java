package com.example.restaurantapps.data.remote.response;

import com.google.gson.annotations.SerializedName;

/**
 * DATA LAYER: DTO (Data Transfer Object)
 * 
 * DTO adalah object untuk transfer data dari API.
 * Kemudian di-convert ke domain Restaurant entity.
 */
public class RestaurantDto {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("pictureId")
    private String pictureId;
    
    @SerializedName("city")
    private String city;
    
    @SerializedName("rating")
    private double rating;

    public RestaurantDto() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureId() {
        return pictureId;
    }

    public String getCity() {
        return city;
    }

    public double getRating() {
        return rating;
    }
}

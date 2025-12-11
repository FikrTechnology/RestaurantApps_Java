package com.example.restaurantapps.model;

import com.google.gson.annotations.SerializedName;

/**
 * STEP 2: MODEL CLASS - Restaurant
 * 
 * Class ini berfungsi sebagai blueprint/template untuk data restaurant.
 * Setiap variabel di sini merepresentasikan field yang ada di JSON response API.
 * 
 * @SerializedName digunakan oleh Gson untuk mapping otomatis dari JSON ke Java Object.
 * Contoh: JSON field "pictureId" akan otomatis di-map ke variable pictureId.
 */
public class Restaurant {
    
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

    public Restaurant() {
    }

    public Restaurant(String id, String name, String description, String pictureId, String city, double rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pictureId = pictureId;
        this.city = city;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    // Helper method to get picture URL
    public String getPictureUrl() {
        return "https://restaurant-api.dicoding.dev/images/medium/" + pictureId;
    }

    @Override
    public String toString() {
        return name + " - " + city + " (" + rating + "⭐)";
    }
}

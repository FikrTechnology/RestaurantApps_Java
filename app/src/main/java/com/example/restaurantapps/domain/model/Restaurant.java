package com.example.restaurantapps.domain.model;

/**
 * DOMAIN LAYER: Entity
 * 
 * Representasi data yang independent dari storage atau API.
 * Class ini adalah "pure data" tanpa logic bisnis.
 */
public class Restaurant {
    private String id;
    private String name;
    private String description;
    private String pictureId;
    private String city;
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

    // Getters
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

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPictureUrl() {
        return "https://restaurant-api.dicoding.dev/images/medium/" + pictureId;
    }

    @Override
    public String toString() {
        return name + " - " + city + " (" + rating + "⭐)";
    }
}

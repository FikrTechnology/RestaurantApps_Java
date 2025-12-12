package com.example.restaurantapps.data.remote.api;

import com.example.restaurantapps.data.remote.response.RestaurantResponse;
import retrofit2.http.GET;

/**
 * DATA LAYER: API Interface
 * 
 * Interface untuk mendefinisikan endpoint API.
 * Retrofit akan generate implementation secara otomatis.
 */
public interface RestaurantApiService {
    
    @GET("list")
    retrofit2.Call<RestaurantResponse> getRestaurantList();
}

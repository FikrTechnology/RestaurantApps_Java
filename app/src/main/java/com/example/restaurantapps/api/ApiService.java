package com.example.restaurantapps.api;

import com.example.restaurantapps.model.RestaurantResponse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * STEP 3: API SERVICE - Interface untuk endpoint API
 * 
 * Interface ini mendefinisikan semua endpoint API yang tersedia.
 * Retrofit akan mengimplementasikan interface ini secara otomatis.
 * 
 * @GET("list") → Menandakan HTTP GET request ke endpoint "list"
 * URL lengkap: BASE_URL + "list" = https://restaurant-api.dicoding.dev/list
 * 
 * Call<RestaurantResponse> → Return type yang merepresentasikan async API call
 */
public interface ApiService {
    
    @GET("list")
    Call<RestaurantResponse> getRestaurantList();
}

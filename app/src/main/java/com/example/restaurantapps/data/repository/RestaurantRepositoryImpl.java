package com.example.restaurantapps.data.repository;

import com.example.restaurantapps.data.remote.api.RestaurantApiService;
import com.example.restaurantapps.data.remote.response.RestaurantResponse;
import com.example.restaurantapps.data.remote.response.RestaurantDto;
import com.example.restaurantapps.domain.model.Restaurant;
import com.example.restaurantapps.domain.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DATA LAYER: Repository Implementation
 * 
 * Menghubungkan domain dan data layer.
 * 
 * Flow:
 * 1. ViewModel memanggil getRestaurants()
 * 2. Repository memanggil API via RestaurantApiService
 * 3. API return RestaurantDto (dari JSON)
 * 4. Repository konversi DTO → Domain Restaurant
 * 5. Repository return domain Restaurant ke ViewModel
 * 
 * Kenapa konversi? Karena:
 * - Domain tidak boleh tahu API structure (JSON, @SerializedName, dll)
 * - Jika API berubah, cukup ubah DTO, domain tetap aman
 * - Testable: bisa mock DTO → Restaurant tanpa API
 */
public class RestaurantRepositoryImpl implements RestaurantRepository {
    
    private RestaurantApiService apiService;
    
    public RestaurantRepositoryImpl(RestaurantApiService apiService) {
        this.apiService = apiService;
    }
    
    @Override
    public void getRestaurants(RestaurantCallback callback) {
        // Step 1: Panggil API
        apiService.getRestaurantList().enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                // Step 2: Cek apakah response berhasil
                if (response.isSuccessful() && response.body() != null) {
                    RestaurantResponse restaurantResponse = response.body();
                    
                    // Step 3: Ambil list RestaurantDto dari response
                    List<RestaurantDto> dtoList = restaurantResponse.getRestaurants();
                    
                    // Step 4: Konversi DTO ke domain Restaurant
                    List<Restaurant> restaurants = convertDtoToDomain(dtoList);
                    
                    // Step 5: Return hasil ke ViewModel
                    callback.onSuccess(restaurants);
                } else {
                    callback.onError("Response tidak berhasil");
                }
            }
            
            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                // API error (network, timeout, dll)
                callback.onError("Error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Konversi RestaurantDto (API layer) → Restaurant (Domain)
     * 
     * Penting! Ini adalah Translation layer:
     * - DTO adalah struktur API (from JSON)
     * - Domain Restaurant adalah model bisnis (pure Java)
     * 
     * Keuntungan:
     * 1. Decoupling: Domain tidak tahu API format
     * 2. Flexibility: Bisa add business logic waktu konversi
     * 3. Testing: Bisa test konversi tanpa API
     */
    private List<Restaurant> convertDtoToDomain(List<RestaurantDto> dtoList) {
        List<Restaurant> restaurants = new ArrayList<>();
        
        for (RestaurantDto dto : dtoList) {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(dto.getId());
            restaurant.setName(dto.getName());
            restaurant.setDescription(dto.getDescription());
            restaurant.setPictureId(dto.getPictureId());
            restaurant.setCity(dto.getCity());
            restaurant.setRating(dto.getRating());
            
            restaurants.add(restaurant);
        }
        
        return restaurants;
    }
}

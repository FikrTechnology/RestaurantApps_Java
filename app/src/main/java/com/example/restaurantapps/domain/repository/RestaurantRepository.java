package com.example.restaurantapps.domain.repository;

import com.example.restaurantapps.domain.model.Restaurant;
import java.util.List;

/**
 * DOMAIN LAYER: Repository Interface
 * 
 * Interface ini TIDAK tahu bagaimana data didapatkan (API atau Database).
 * Activity/ViewModel cukup implementasi interface ini, bukan API langsung.
 * 
 * Keuntungan:
 * 1. Testing: Bisa mock tanpa API
 * 2. Maintainability: Bisa ganti API → Database tanpa ubah Activity
 * 3. Separation of Concerns: Activity fokus UI, Repository fokus data
 */
public interface RestaurantRepository {
    
    /**
     * Dapatkan daftar restaurant dari sumber data apapun
     * 
     * Interview Q: "Kenapa interface di domain layer?"
     * A: Karena domain tidak boleh tergantung teknologi (API, DB).
     *    Interface adalah kontrak: "Siapapun yang implementasi, harus sediakan data"
     */
    void getRestaurants(RestaurantCallback callback);
    
    interface RestaurantCallback {
        void onSuccess(List<Restaurant> restaurants);
        void onError(String error);
    }
}

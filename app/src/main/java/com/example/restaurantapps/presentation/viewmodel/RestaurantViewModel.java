package com.example.restaurantapps.presentation.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.restaurantapps.data.remote.api.RetrofitClient;
import com.example.restaurantapps.data.repository.RestaurantRepositoryImpl;
import com.example.restaurantapps.domain.model.Restaurant;
import com.example.restaurantapps.domain.repository.RestaurantRepository;

import java.util.List;

/**
 * PRESENTATION LAYER: ViewModel
 * 
 * Fungsi ViewModel:
 * 1. Manage UI state (data yang ditampilkan)
 * 2. Survive configuration changes (rotate screen, dll)
 * 3. Tidak hold reference ke Activity/Fragment (prevent memory leak)
 * 4. Berisi business logic (fetch data, filter, dll)
 * 
 * Interview Q: "Apa perbedaan ViewModel vs Activity?"
 * A: Activity di-recreate saat rotate screen, tapi ViewModel TIDAK.
 *    Jadi data tidak hilang saat rotate.
 * 
 * Flow MVVM:
 * Activity → ViewModel → Repository → API
 * API response → Repository → ViewModel (update LiveData) → Activity (observe)
 */
public class RestaurantViewModel extends AndroidViewModel {
    
    // LiveData adalah reactive wrapper
    // Semua observer (Activity/Fragment) akan di-notify jika value berubah
    private MutableLiveData<List<Restaurant>> restaurantList = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    private RestaurantRepository repository;
    
    public RestaurantViewModel(@NonNull Application application) {
        super(application);
        // Initialize repository dengan API service
        repository = new RestaurantRepositoryImpl(RetrofitClient.getApiService());
    }
    
    /**
     * LiveData getter: Activity akan observe ini
     * 
     * Interview Q: "Kenapa return LiveData, bukan List<Restaurant>?"
     * A: LiveData adalah observable - saat value berubah, semua observer di-notify otomatis.
     *    Activity tidak perlu polling atau callback.
     */
    public MutableLiveData<List<Restaurant>> getRestaurantList() {
        return restaurantList;
    }
    
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * Fetch restaurants dari repository
     * 
     * Interview Q: "Apa yang terjadi saat loadRestaurants() dipanggil?"
     * A:
     * 1. Set isLoading = true (tampilkan progress bar)
     * 2. Panggil repository.getRestaurants()
     * 3. Saat response datang, update restaurantList LiveData
     * 4. Activity observe restaurantList, otomatis update UI
     * 5. Set isLoading = false (hide progress bar)
     */
    public void loadRestaurants() {
        isLoading.setValue(true);
        errorMessage.setValue(null); // Clear previous error
        
        repository.getRestaurants(new RestaurantRepository.RestaurantCallback() {
            @Override
            public void onSuccess(List<Restaurant> restaurants) {
                // Update LiveData dengan data
                restaurantList.setValue(restaurants);
                isLoading.setValue(false);
            }
            
            @Override
            public void onError(String error) {
                // Update error LiveData
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }
}

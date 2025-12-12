package com.example.restaurantapps.presentation.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.restaurantapps.R;
import com.example.restaurantapps.domain.model.Restaurant;
import com.example.restaurantapps.presentation.ui.adapter.RestaurantAdapter;
import com.example.restaurantapps.presentation.viewmodel.RestaurantViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * PRESENTATION LAYER: MainActivity (MVVM)
 * 
 * Tanggung jawab Activity HANYA untuk UI:
 * - Observe ViewModel data
 * - Update UI berdasarkan data
 * - Handle user interactions
 * 
 * BUKAN untuk:
 * - API calls (Repository)
 * - Business logic (ViewModel)
 * - Data transformation (Repository)
 * 
 * Interview Q: "Apa peran Activity dalam MVVM?"
 * A: Activity adalah "dumb view" - hanya menampilkan data dari ViewModel.
 *    Semua logic ada di ViewModel, Activity hanya observers.
 */
public class MainActivity extends AppCompatActivity {
    
    private GridView gridViewRestaurant;
    private ProgressBar progressBar;
    private RestaurantAdapter adapter;
    private RestaurantViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Step 1: Initialize UI components
        initializeViews();
        
        // Step 2: Create adapter (empty list first)
        adapter = new RestaurantAdapter(this, new ArrayList<>());
        gridViewRestaurant.setAdapter(adapter);
        
        // Step 3: Get ViewModel instance
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        
        // Step 4: Observe LiveData dari ViewModel
        observeViewModel();
        
        // Step 5: Load data
        viewModel.loadRestaurants();
    }
    
    /**
     * Initialize semua UI components
     */
    private void initializeViews() {
        gridViewRestaurant = findViewById(R.id.gridViewRestaurant);
        progressBar = findViewById(R.id.progressBar);
    }
    
    /**
     * Observe ViewModel's LiveData
     * 
     * "Observer pattern": Saat LiveData value berubah, observer callback dipanggil.
     * 
     * Interview Q: "Apa bedanya observer di Activity vs polling di background thread?"
     * A: Observer = reactive (dipanggil saat value berubah)
     *    Polling = aktif cek value terus-terusan (boros battery)
     *    Observer lebih efficient.
     */
    private void observeViewModel() {
        // Observe restaurant list
        viewModel.getRestaurantList().observe(this, restaurants -> {
            if (restaurants != null && !restaurants.isEmpty()) {
                // Update adapter dengan data baru
                adapter.clear();
                adapter.addAll(restaurants);
                adapter.notifyDataSetChanged();
            }
        });
        
        // Observe error message
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                // Tampilkan error ke user
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                // Show/hide progress bar
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }
}

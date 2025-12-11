package com.example.restaurantapps;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurantapps.adapter.RestaurantAdapter;
import com.example.restaurantapps.api.ApiService;
import com.example.restaurantapps.api.RetrofitClient;
import com.example.restaurantapps.model.Restaurant;
import com.example.restaurantapps.model.RestaurantResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * STEP 4: MAIN ACTIVITY - UI dan Logic Aplikasi
 * 
 * Activity ini adalah entry point aplikasi yang menampilkan list restaurant dalam GridView.
 * 
 * PERUBAHAN dari ListView ke GridView:
 * - ListView → GridView (2 column layout)
 * - ArrayAdapter → RestaurantAdapter (custom adapter)
 * - Simple list item → Card dengan gambar, nama, kota, rating
 * 
 * Flow:
 * 1. Setup UI (GridView, Custom Adapter)
 * 2. Inisialisasi API Service
 * 3. Fetch data dari API
 * 4. Handle response (success/failure)
 * 5. Update GridView dengan data yang didapat
 */
public class MainActivity extends AppCompatActivity {
    // Tag untuk logging
    private static final String TAG = "MainActivity";
    
    // UI Components
    GridView gridView;                          // GridView untuk menampilkan restaurant dalam grid
    ArrayList<Restaurant> restaurantList;       // Data source untuk adapter
    RestaurantAdapter adapter;                  // Custom adapter untuk GridView
    
    // API Service
    ApiService apiService;                      // Interface untuk API calls

    /**
     * STEP 4.1: onCreate - Inisialisasi Activity
     * Method ini dipanggil pertama kali saat Activity dibuat
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // STEP 4.2: Setup GridView dengan Custom Adapter
        gridView = findViewById(R.id.grid_view);  // Ambil reference GridView dari layout
        restaurantList = new ArrayList<>();        // Buat ArrayList kosong untuk data
        
        // STEP 4.2.8: Buat instance RestaurantAdapter (custom adapter)
        // RestaurantAdapter akan handle binding data ke card layout
        adapter = new RestaurantAdapter(this, restaurantList);
        gridView.setAdapter(adapter);             // Set adapter ke GridView

        // STEP 4.3: Initialize API Service
        // Dapatkan instance ApiService dari RetrofitClient
        apiService = RetrofitClient.getApiService();

        // STEP 4.4: Fetch data dari API
        fetchRestaurantList();
    }

    /**
     * STEP 4.5: Method untuk fetch data restaurant dari API
     * Menggunakan asynchronous call agar tidak blocking UI Thread
     */
    private void fetchRestaurantList() {
        // STEP 4.6: Buat API Call
        Call<RestaurantResponse> call = apiService.getRestaurantList();
        
        // STEP 4.7: Eksekusi request secara asynchronous
        // enqueue() akan menjalankan request di background thread
        call.enqueue(new Callback<RestaurantResponse>() {
            /**
             * STEP 4.8: Callback saat response berhasil diterima
             * Method ini dipanggil di Main Thread, aman untuk update UI
             */
            @Override
            public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                // STEP 4.9: Cek apakah HTTP response sukses (200-299)
                if (response.isSuccessful() && response.body() != null) {
                    RestaurantResponse restaurantResponse = response.body();
                    
                    // STEP 4.10: Cek apakah ada error dari API
                    if (!restaurantResponse.isError()) {
                        // STEP 4.11: Ambil list restaurant dari response
                        List<Restaurant> restaurants = restaurantResponse.getRestaurants();
                        
                        // STEP 4.12: Update data di ArrayList
                        restaurantList.clear();              // Kosongkan data lama
                        restaurantList.addAll(restaurants);  // Tambahkan data baru
                        adapter.notifyDataSetChanged();      // PENTING: Beritahu adapter data berubah
                        
                        // STEP 4.13: Log dan tampilkan pesan sukses
                        Log.d(TAG, "Success: Loaded " + restaurantResponse.getCount() + " restaurants");
                        Toast.makeText(MainActivity.this, 
                                "Berhasil memuat " + restaurantResponse.getCount() + " restaurant", 
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Error dari API (error: true)
                        Log.e(TAG, "Error from API: " + restaurantResponse.getMessage());
                        Toast.makeText(MainActivity.this, 
                                "Error: " + restaurantResponse.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // HTTP error (404, 500, dll)
                    Log.e(TAG, "Response not successful: " + response.code());
                    Toast.makeText(MainActivity.this, 
                            "Gagal memuat data: " + response.code(), 
                            Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * STEP 4.14: Callback saat terjadi error/failure
             * Dipanggil jika terjadi network error, timeout, atau exception lainnya
             */
            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                // STEP 4.15: Log error untuk debugging
                Log.e(TAG, "API Call failed: " + t.getMessage(), t);
                
                // STEP 4.16: Tampilkan pesan error ke user
                Toast.makeText(MainActivity.this, 
                        "Gagal koneksi: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
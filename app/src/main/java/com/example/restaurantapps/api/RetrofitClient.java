package com.example.restaurantapps.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * STEP 3: RETROFIT CLIENT - Singleton untuk API Client
 * 
 * Class ini bertanggung jawab untuk:
 * 1. Membuat instance Retrofit dengan konfigurasi yang tepat
 * 2. Memastikan hanya ada 1 instance Retrofit (Singleton Pattern)
 * 3. Menyediakan ApiService yang siap digunakan
 * 
 * Konfigurasi yang disetup:
 * - Base URL: https://restaurant-api.dicoding.dev/
 * - Gson Converter: Untuk convert JSON <-> Java Object
 * - Logging Interceptor: Untuk debug network request/response
 */
public class RetrofitClient {
    
    // Base URL dari Restaurant API Dicoding
    private static final String BASE_URL = "https://restaurant-api.dicoding.dev/";
    
    // Instance Retrofit yang akan di-reuse (Singleton)
    private static Retrofit retrofit = null;

    /**
     * Method untuk mendapatkan instance Retrofit
     * Menggunakan Singleton Pattern - hanya membuat 1 instance
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // STEP 3.1: Setup Logging Interceptor
            // Untuk melihat detail request & response di Logcat (debugging)
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // STEP 3.2: Setup OkHttpClient
            // Client HTTP yang akan digunakan Retrofit untuk network request
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // STEP 3.3: Build Retrofit Instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // Set base URL
                    .client(client)     // Set HTTP client
                    .addConverterFactory(GsonConverterFactory.create())  // Set JSON converter
                    .build();
        }
        return retrofit;
    }

    /**
     * Method untuk mendapatkan ApiService yang siap digunakan
     * Retrofit akan mengimplementasikan interface ApiService secara otomatis
     */
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}

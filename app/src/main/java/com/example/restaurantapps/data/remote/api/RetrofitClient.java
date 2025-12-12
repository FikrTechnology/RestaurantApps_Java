package com.example.restaurantapps.data.remote.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DATA LAYER: Retrofit Configuration (Singleton)
 * 
 * Konfigurasi Retrofit client.
 * Hanya dibuat 1 instance (Singleton pattern) untuk efficiency.
 */
public class RetrofitClient {
    
    private static final String BASE_URL = "https://restaurant-api.dicoding.dev/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Setup logging untuk debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Setup OkHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Build Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static RestaurantApiService getApiService() {
        return getClient().create(RestaurantApiService.class);
    }
}

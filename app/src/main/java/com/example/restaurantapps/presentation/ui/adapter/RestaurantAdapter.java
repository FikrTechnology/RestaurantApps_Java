package com.example.restaurantapps.presentation.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.restaurantapps.R;
import com.example.restaurantapps.domain.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * PRESENTATION LAYER: RestaurantAdapter
 * 
 * Adapter adalah "bridge" antara data dan UI components di GridView.
 * Adapter menentukan: bagaimana data ditampilkan di setiap cell.
 * 
 * ViewHolder pattern:
 * - Menghindari findViewById() berulang kali
 * - Lebih efficient (caching references)
 * 
 * Interview Q: "Kenapa perlu ViewHolder pattern?"
 * A: GridView menggunakan cell recycling (reuse cell saat scroll).
 *    ViewHolder menyimpan references supaya tidak perlu findViewById lagi.
 *    Tanpa ViewHolder, setiap bind cell= findViewById berulang kali = lag.
 */
public class RestaurantAdapter extends BaseAdapter {
    
    private Context context;
    private List<Restaurant> restaurants;
    
    public RestaurantAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }
    
    @Override
    public int getCount() {
        return restaurants != null ? restaurants.size() : 0;
    }
    
    @Override
    public Object getItem(int position) {
        return restaurants.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    /**
     * Ini method PENTING - dipanggil untuk setiap cell di GridView
     * 
     * Interview Q: "Berapa kali getView() dipanggil saat scroll?"
     * A: Hanya untuk cell yang VISIBLE. Jika 10 cell visible, getView() dipanggil 10x.
     *    Saat scroll, getView() dipanggil lagi untuk cell yang di-recycle.
     *    Tanpa ViewHolder, setiap getView() = 5 findViewById() = lag.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            // Step 1: Inflate layout untuk first time
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_item_restaurant, parent, false);
            
            // Step 2: Create ViewHolder (cache references)
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageViewRestaurant);
            holder.nameTextView = convertView.findViewById(R.id.textViewName);
            holder.cityTextView = convertView.findViewById(R.id.textViewCity);
            holder.ratingBar = convertView.findViewById(R.id.ratingBarRestaurant);
            holder.cardView = convertView.findViewById(R.id.cardViewRestaurant);
            
            // Step 3: Attach holder ke view (untuk reuse nanti)
            convertView.setTag(holder);
        } else {
            // Step 1: Reuse existing ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }
        
        // Step 2: Get data dari list
        Restaurant restaurant = restaurants.get(position);
        
        // Step 3: Bind data ke UI components
        holder.nameTextView.setText(restaurant.getName());
        holder.cityTextView.setText(restaurant.getCity());
        holder.ratingBar.setRating((float) restaurant.getRating());
        
        // Step 4: Load image dengan Glide
        String imageUrl = "https://restaurant-api.dicoding.dev/images/small/" + restaurant.getPictureId();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background) // Placeholder saat loading
                .error(R.drawable.ic_launcher_background) // Error image jika gagal
                .into(holder.imageView);
        
        return convertView;
    }
    
    /**
     * Helper method untuk update adapter data
     */
    public void clear() {
        restaurants.clear();
    }
    
    public void addAll(List<Restaurant> newRestaurants) {
        restaurants.addAll(newRestaurants);
    }
    
    /**
     * ViewHolder pattern: cache references supaya efficient
     * 
     * Jangan declare variable di getView() → jangan di-recycle saat convert != null
     * Declare di ViewHolder static class → reuse across cell recycles
     */
    private static class ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView cityTextView;
        RatingBar ratingBar;
        CardView cardView;
    }
}

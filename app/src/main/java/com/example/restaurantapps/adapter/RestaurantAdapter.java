package com.example.restaurantapps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.restaurantapps.R;
import com.example.restaurantapps.model.Restaurant;

import java.util.List;

/**
 * STEP 4.2.8: Custom Adapter untuk GridView
 * 
 * Adapter ini bertanggung jawab untuk:
 * 1. Convert data Restaurant menjadi View untuk GridView
 * 2. Bind data ke setiap item card
 * 3. Load gambar menggunakan Glide
 * 
 * BaseAdapter adalah parent class untuk custom adapter
 */
public class RestaurantAdapter extends BaseAdapter {

    // STEP 4.2.9: Variabel instance
    private Context context;                     // Konteks aplikasi
    private List<Restaurant> restaurantList;     // List data restaurant
    private LayoutInflater layoutInflater;       // Untuk inflate layout XML

    /**
     * STEP 4.2.10: Constructor
     * Menerima context dan list restaurant
     */
    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * STEP 4.2.11: Method getCount()
     * Return jumlah item yang akan ditampilkan
     */
    @Override
    public int getCount() {
        return restaurantList.size();
    }

    /**
     * STEP 4.2.12: Method getItem()
     * Return object item pada posisi tertentu
     */
    @Override
    public Object getItem(int position) {
        return restaurantList.get(position);
    }

    /**
     * STEP 4.2.13: Method getItemId()
     * Return ID item (biasanya sama dengan position)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * STEP 4.2.14: Method getView()
     * Method PALING PENTING - di sini kita bind data ke view
     * Dipanggil untuk setiap item yang ditampilkan
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // STEP 4.2.15: ViewHolder pattern untuk efisiensi
        ViewHolder holder;

        // STEP 4.2.16: Cek apakah convertView sudah di-inflate sebelumnya
        if (convertView == null) {
            // Pertama kali, inflate layout baru
            convertView = layoutInflater.inflate(R.layout.grid_item_restaurant, parent, false);

            // STEP 4.2.17: Inisialisasi ViewHolder
            holder = new ViewHolder();
            holder.ivImage = convertView.findViewById(R.id.iv_restaurant_image);
            holder.tvName = convertView.findViewById(R.id.tv_restaurant_name);
            holder.tvCity = convertView.findViewById(R.id.tv_restaurant_city);
            holder.tvRating = convertView.findViewById(R.id.tv_restaurant_rating);

            // STEP 4.2.18: Set tag untuk reuse holder
            convertView.setTag(holder);
        } else {
            // Reuse holder dari cache
            holder = (ViewHolder) convertView.getTag();
        }

        // STEP 4.2.19: Ambil data restaurant pada posisi ini
        Restaurant restaurant = restaurantList.get(position);

        // STEP 4.2.20: Bind data ke view
        // Set nama restaurant
        holder.tvName.setText(restaurant.getName());

        // Set kota/tempat
        holder.tvCity.setText(restaurant.getCity());

        // Set rating
        holder.tvRating.setText(String.valueOf(restaurant.getRating()));

        // STEP 4.2.21: Load gambar menggunakan Glide
        // URL gambar format: https://restaurant-api.dicoding.dev/images/medium/{pictureId}
        String imageUrl = "https://restaurant-api.dicoding.dev/images/medium/" + restaurant.getPictureId();
        
        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .into(holder.ivImage);

        return convertView;
    }

    /**
     * STEP 4.2.22: ViewHolder Class
     * Class statis untuk cache view references
     * Ini meningkatkan performa karena tidak perlu findViewById() berulang kali
     */
    static class ViewHolder {
        ImageView ivImage;      // ImageView untuk gambar
        TextView tvName;        // TextView untuk nama
        TextView tvCity;        // TextView untuk kota
        TextView tvRating;      // TextView untuk rating
    }
}

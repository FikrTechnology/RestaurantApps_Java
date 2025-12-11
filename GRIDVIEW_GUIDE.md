# 🎨 GridView Implementation Guide

Dokumentasi untuk perubahan dari ListView menjadi GridView dengan Custom Card Layout.

---

## 📊 Perubahan Utama

### **Sebelum (ListView):**
```
┌─────────────────────────────────┐
│ Restaurant 1 - Kota (4.2⭐)     │
├─────────────────────────────────┤
│ Restaurant 2 - Kota (4.0⭐)     │
├─────────────────────────────────┤
│ Restaurant 3 - Kota (4.6⭐)     │
└─────────────────────────────────┘
```

### **Sesudah (GridView dengan Card):**
```
┌──────────────────┬──────────────────┐
│  [IMAGE]         │  [IMAGE]         │
│ Restaurant 1     │ Restaurant 2     │
│ Medan (4.2⭐)   │ Gorontalo (4⭐) │
└──────────────────┴──────────────────┘
┌──────────────────┬──────────────────┐
│  [IMAGE]         │  [IMAGE]         │
│ Restaurant 3     │ Restaurant 4     │
│ Medan (4.6⭐)   │ Bandung (4.4⭐) │
└──────────────────┴──────────────────┘
```

---

## 🔧 Perubahan Technical

### **1. Layout (XML)**

#### **activity_main.xml**
```xml
<!-- SEBELUM: ListView -->
<ListView
    android:id="@+id/my_list_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"/>

<!-- SESUDAH: GridView (2 column) -->
<GridView
    android:id="@+id/grid_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:numColumns="2"          <!-- 2 column layout -->
    android:padding="8dp"
    android:horizontalSpacing="8dp"  <!-- Spacing antar column -->
    android:verticalSpacing="8dp"    <!-- Spacing antar row -->
/>
```

**GridView Attributes:**
- `android:numColumns="2"` - Jumlah column (2 berarti 2 column)
- `android:horizontalSpacing="8dp"` - Jarak horizontal antar item
- `android:verticalSpacing="8dp"` - Jarak vertikal antar item

#### **grid_item_restaurant.xml** (New)
File layout baru untuk setiap item card di GridView.

```xml
<androidx.cardview.widget.CardView>
    <!-- Card dengan rounded corners & shadow -->
    
    <LinearLayout orientation="vertical">
        
        <!-- ImageView: Gambar Restaurant -->
        <ImageView
            android:id="@+id/iv_restaurant_image"
            android:layout_height="160dp"
            android:scaleType="centerCrop" />
        
        <!-- Text Container -->
        <LinearLayout orientation="vertical" android:padding="12dp">
            
            <!-- Nama Restaurant -->
            <TextView android:id="@+id/tv_restaurant_name" />
            
            <!-- Kota -->
            <TextView android:id="@+id/tv_restaurant_city" />
            
            <!-- Rating -->
            <LinearLayout orientation="horizontal">
                <TextView android:id="@+id/tv_restaurant_rating" />
                <TextView android:text=" ⭐" />
            </LinearLayout>
            
        </LinearLayout>
    </LinearLayout>
</androidx.cardview.widget.CardView>
```

---

### **2. Java Code**

#### **MainActivity.java Changes**

**SEBELUM:**
```java
ListView listView;
ArrayList<Restaurant> restaurantList;
ArrayAdapter<Restaurant> adapter;

@Override
protected void onCreate(Bundle savedInstanceState) {
    listView = findViewById(R.id.my_list_view);
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurantList);
    listView.setAdapter(adapter);
}
```

**SESUDAH:**
```java
GridView gridView;
ArrayList<Restaurant> restaurantList;
RestaurantAdapter adapter;  // Custom adapter

@Override
protected void onCreate(Bundle savedInstanceState) {
    gridView = findViewById(R.id.grid_view);
    adapter = new RestaurantAdapter(this, restaurantList);  // Custom adapter
    gridView.setAdapter(adapter);
}
```

#### **RestaurantAdapter.java** (New - Custom Adapter)

```java
public class RestaurantAdapter extends BaseAdapter {
    private Context context;
    private List<Restaurant> restaurantList;
    private LayoutInflater layoutInflater;
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // STEP 4.2.15: ViewHolder pattern untuk efisiensi
        
        if (convertView == null) {
            // Inflate layout baru
            convertView = layoutInflater.inflate(R.layout.grid_item_restaurant, ...);
            
            // Setup ViewHolder
            holder = new ViewHolder();
            holder.ivImage = convertView.findViewById(R.id.iv_restaurant_image);
            holder.tvName = convertView.findViewById(R.id.tv_restaurant_name);
            holder.tvCity = convertView.findViewById(R.id.tv_restaurant_city);
            holder.tvRating = convertView.findViewById(R.id.tv_restaurant_rating);
        }
        
        // Bind data
        Restaurant restaurant = restaurantList.get(position);
        holder.tvName.setText(restaurant.getName());
        holder.tvCity.setText(restaurant.getCity());
        holder.tvRating.setText(String.valueOf(restaurant.getRating()));
        
        // Load gambar dengan Glide
        Glide.with(context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.ivImage);
        
        return convertView;
    }
    
    static class ViewHolder {
        ImageView ivImage;
        TextView tvName;
        TextView tvCity;
        TextView tvRating;
    }
}
```

---

### **3. Dependencies**

**Ditambahkan:**
```gradle
// For Image Loading
implementation(libs.glide)  // Glide 4.15.1

// For Card Widget
implementation(libs.cardview)  // CardView 1.0.0
```

---

## 🎯 Flow Perubahan (Step by Step)

```
STEP 4.2: Setup UI (SEBELUM)
    ↓
    listView = findViewById(R.id.my_list_view)
    adapter = new ArrayAdapter<>(...)
    listView.setAdapter(adapter)

DIUBAH MENJADI:

STEP 4.2: Setup UI (SESUDAH)
    ↓
    gridView = findViewById(R.id.grid_view)  // GridView instead of ListView
    adapter = new RestaurantAdapter(this, restaurantList)  // Custom adapter
    gridView.setAdapter(adapter)
```

---

## 🏗️ Struktur File Baru

```
app/src/main/
├── res/
│   └── layout/
│       ├── activity_main.xml          (UPDATED: ListView → GridView)
│       └── grid_item_restaurant.xml   (NEW: Card layout)
│
└── java/com/example/restaurantapps/
    ├── adapter/
    │   └── RestaurantAdapter.java     (NEW: Custom adapter untuk GridView)
    │
    ├── MainActivity.java              (UPDATED: GridView logic)
    └── ... (files lainnya tetap sama)
```

---

## 📱 Penjelasan Komponen Grid

### **1. GridView Attributes**

```xml
<GridView
    android:id="@+id/grid_view"
    android:numColumns="2"              <!-- 2 column (lebar sama otomatis) -->
    android:horizontalSpacing="8dp"     <!-- Space antar column -->
    android:verticalSpacing="8dp"       <!-- Space antar row -->
    android:padding="8dp"               <!-- Padding container -->
/>
```

**numColumns options:**
- `android:numColumns="2"` - Fixed 2 column
- `android:numColumns="auto_fit"` - Adaptive based on screen width
- `android:numColumns="3"` - 3 column, dst

### **2. CardView Features**

```xml
<androidx.cardview.widget.CardView
    app:cardCornerRadius="12dp"         <!-- Sudut rounded -->
    app:cardElevation="4dp"             <!-- Shadow/elevation -->
    android:layout_margin="4dp"         <!-- Margin around card -->
/>
```

**CardView Advantages:**
- ✅ Material Design
- ✅ Rounded corners otomatis
- ✅ Shadow/elevation
- ✅ Ripple effect on click

### **3. Custom Adapter (RestaurantAdapter)**

**Method penting:**

```java
// Return jumlah item
getCount() → restaurantList.size()

// Return object item
getItem(position) → restaurantList.get(position)

// Return ID item
getItemId(position) → position

// Paling penting - bind data ke view
getView(position, convertView, parent) {
    if (convertView == null) {
        // Inflate layout baru
        convertView = layoutInflater.inflate(R.layout.grid_item_restaurant, ...)
        // Setup ViewHolder
    }
    
    // Bind data
    Restaurant restaurant = restaurantList.get(position);
    holder.tvName.setText(restaurant.getName());
    // ... set other views
    
    return convertView;
}
```

### **4. ViewHolder Pattern**

**Kenapa penting?**

```
TANPA ViewHolder (BURUK ❌):
- findViewById() dipanggil setiap kali getView()
- Lambat karena traversing XML tree
- Banyak garbage collection

DENGAN ViewHolder (BAIK ✅):
- findViewById() hanya 1x (pertama kali)
- Reuse holder via setTag()
- Lebih cepat dan efisien
```

```java
static class ViewHolder {
    ImageView ivImage;
    TextView tvName;
    TextView tvCity;
    TextView tvRating;
}

// Cache holder
convertView.setTag(holder);

// Reuse holder
holder = (ViewHolder) convertView.getTag();
```

### **5. Glide Image Loading**

```java
Glide.with(context)
    .load(imageUrl)
    .centerCrop()           // Crop to fit
    .into(holder.ivImage)   // Set ke ImageView
```

**Glide advantages:**
- ✅ Memory efficient
- ✅ Image caching
- ✅ Placeholder support
- ✅ Error handling

---

## 🎨 Customization Tips

### **Ubah jumlah column:**
```xml
<!-- 3 column -->
android:numColumns="3"

<!-- Adaptive (auto fit based on width) -->
android:numColumns="auto_fit"
android:columnWidth="200dp"
```

### **Ubah spacing:**
```xml
<!-- Lebih lebar -->
android:horizontalSpacing="16dp"
android:verticalSpacing="16dp"

<!-- Lebih sempit -->
android:horizontalSpacing="4dp"
android:verticalSpacing="4dp"
```

### **Ubah card style:**
```xml
<!-- Card lebih besar -->
<ImageView
    android:layout_height="200dp"  <!-- Default 160dp -->
/>

<!-- Card tanpa shadow -->
<androidx.cardview.widget.CardView
    app:cardElevation="0dp"        <!-- Remove shadow -->
/>

<!-- Card dengan border -->
<androidx.cardview.widget.CardView
    app:cardBackgroundColor="@android:color/white"
    app:strokeColor="@android:color/darker_gray"
    app:strokeWidth="1dp"
/>
```

---

## 📊 Performance Comparison

| Aspek | ListView + ArrayAdapter | GridView + Custom Adapter |
|-------|-------------------------|--------------------------|
| Layout | Simple (1 column) | Advanced (N columns) |
| Flexibility | Limited | High |
| Image Support | Manual | Built-in with Glide |
| Performance | Good | Better (ViewHolder) |
| Customization | Limited | Full control |
| Code Complexity | Low | Medium |

---

## ✅ Checklist Implementation

- [x] Update `activity_main.xml` (ListView → GridView)
- [x] Create `grid_item_restaurant.xml` (Card layout)
- [x] Create `RestaurantAdapter.java` (Custom adapter)
- [x] Update `MainActivity.java` (GridView logic)
- [x] Add Glide dependency (Image loading)
- [x] Add CardView dependency (Card widget)
- [x] Update `libs.versions.toml` (Dependencies)
- [x] Update `build.gradle.kts` (Dependencies)

---

## 🔍 Debugging Tips

### **GridView tidak menampilkan item:**
1. Cek `getCount()` return value
2. Cek apakah adapter di-set dengan benar
3. Cek Logcat untuk error

### **Gambar tidak muncul:**
1. Cek URL gambar valid
2. Cek Glide dependency ter-install
3. Cek INTERNET permission

### **Scrolling lambat:**
1. Pastikan ViewHolder pattern digunakan
2. Jangan bikin object baru di getView()
3. Gunakan image caching (Glide handles ini)

---

## 📖 Sumber Belajar

- [GridView Documentation](https://developer.android.com/reference/android/widget/GridView)
- [BaseAdapter Documentation](https://developer.android.com/reference/android/widget/BaseAdapter)
- [CardView Documentation](https://developer.android.com/reference/androidx/cardview/widget/CardView)
- [Glide Documentation](https://bumptech.github.io/glide/)
- [ViewHolder Pattern](https://developer.android.com/training/improving-layouts/smooth-scrolling)

---

## 🚀 Next Steps (Improvement Ideas)

- [ ] Ganti GridView dengan RecyclerView (lebih modern)
- [ ] Tambahkan click listener untuk item
- [ ] Implementasi pull-to-refresh
- [ ] Tambahkan search/filter
- [ ] Implementasi pagination untuk banyak data
- [ ] Tambahkan animation untuk item entry

---

**Happy Coding! 🎨**

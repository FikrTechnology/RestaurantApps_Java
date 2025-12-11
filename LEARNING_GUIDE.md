# 📘 LEARNING GUIDE - Step by Step

Panduan belajar sistematis untuk memahami flow aplikasi Restaurant Apps.

---

## 🎓 Cara Menggunakan Guide Ini

1. **Baca dari STEP 1 sampai STEP 4** secara berurutan
2. **Buka file yang disebutkan** di Android Studio
3. **Lihat komentar dengan nomor STEP** di setiap file
4. **Pahami konsep** sebelum lanjut ke step berikutnya
5. **Coba modifikasi** untuk eksperimen sendiri

---

## 📋 Urutan Baca File (Flow Learning)

### **STEP 1: Persiapan Project** ⚙️

**File yang harus dibaca:**

1. **`gradle/libs.versions.toml`** - Lihat dependencies yang digunakan
2. **`app/build.gradle.kts`** - Lihat implementasi dependencies
3. **`app/src/main/AndroidManifest.xml`** - Lihat permission INTERNET

**Apa yang dipelajari:**
- Cara menambahkan library Retrofit, Gson, OkHttp
- Kenapa perlu permission INTERNET
- Version management dengan libs.versions.toml

**Konsep Kunci:**
```
Dependencies = Library tambahan yang kita butuhkan
Permission = Izin untuk akses fitur device (Internet, Camera, dll)
```

---

### **STEP 2: Model Classes (Data Structure)** 📦

**File yang harus dibaca (URUTAN PENTING):**

1. **`model/Restaurant.java`** ← Baca dulu!
   - Lihat komentar: `STEP 2: MODEL CLASS - Restaurant`
   - Pahami setiap field (id, name, city, rating)
   - Pahami `@SerializedName` untuk apa

2. **`model/RestaurantResponse.java`** ← Baca kedua!
   - Lihat komentar: `STEP 2: MODEL CLASS - RestaurantResponse`
   - Pahami struktur response API
   - Lihat `List<Restaurant>` yang berisi banyak restaurant

**Apa yang dipelajari:**
- POJO (Plain Old Java Object) untuk mapping JSON
- Anotasi `@SerializedName` dari Gson
- Getter & Setter methods
- Helper method `getPictureUrl()`

**Konsep Kunci:**
```java
// JSON dari API
{
  "name": "Melting Pot",
  "city": "Medan"
}

// Auto-convert jadi Java Object oleh Gson
Restaurant {
  name = "Melting Pot"
  city = "Medan"
}
```

**Latihan:**
- [ ] Coba tambahkan field baru di `Restaurant.java`
- [ ] Coba override method `toString()` dengan format berbeda

---

### **STEP 3: API Layer (Network Configuration)** 🌐

**File yang harus dibaca (URUTAN PENTING):**

1. **`api/ApiService.java`** ← Baca dulu!
   - Lihat komentar: `STEP 3: API SERVICE`
   - Pahami anotasi `@GET("list")`
   - Pahami return type `Call<RestaurantResponse>`

2. **`api/RetrofitClient.java`** ← Baca kedua!
   - Lihat komentar mulai dari: `STEP 3: RETROFIT CLIENT`
   - Pahami **STEP 3.1**: Logging Interceptor
   - Pahami **STEP 3.2**: OkHttpClient
   - Pahami **STEP 3.3**: Retrofit Builder
   - Pahami Singleton Pattern (`if (retrofit == null)`)

**Apa yang dipelajari:**
- Interface untuk definisi endpoint API
- Retrofit configuration (baseUrl, converter, client)
- Logging untuk debugging network request
- Singleton Pattern untuk efisiensi

**Konsep Kunci:**
```java
// Interface ApiService
@GET("list") → Definisi endpoint
Call<RestaurantResponse> → Return type async call

// Retrofit Builder
.baseUrl() → URL dasar API
.addConverterFactory(Gson) → Parser JSON
.client(OkHttp) → HTTP engine
```

**Latihan:**
- [ ] Coba tambah endpoint baru di `ApiService` (misalnya: `@GET("detail/{id}")`)
- [ ] Coba ubah logging level jadi `.HEADERS` atau `.BASIC`

---

### **STEP 4: MainActivity (UI & Business Logic)** 📱

**File yang harus dibaca:**

**`MainActivity.java`** - Baca STEP BY STEP!

#### **Urutan Baca dalam MainActivity:**

**A. STEP 4.1: onCreate() - Entry Point**
```java
protected void onCreate(Bundle savedInstanceState) {
    // Method ini dipanggil pertama kali saat Activity dibuat
}
```
**Pahami:** Ini adalah pintu masuk aplikasi

---

**B. STEP 4.2: Setup ListView**
```java
listView = findViewById(R.id.my_list_view);
restaurantList = new ArrayList<>();
adapter = new ArrayAdapter<>(...);
listView.setAdapter(adapter);
```
**Pahami:**
- `findViewById`: Ambil reference UI dari XML
- `ArrayList`: Container untuk data
- `Adapter`: Bridge antara data dan UI
- Flow: Data → Adapter → ListView

---

**C. STEP 4.3: Initialize API Service**
```java
apiService = RetrofitClient.getApiService();
```
**Pahami:** Dapatkan instance ApiService yang sudah dikonfigurasi

---

**D. STEP 4.4: Fetch Restaurant List**
```java
fetchRestaurantList();
```
**Pahami:** Panggil method untuk mulai fetch data

---

**E. STEP 4.5-4.7: Create & Execute API Call**
```java
// STEP 4.6: Buat Call object
Call<RestaurantResponse> call = apiService.getRestaurantList();

// STEP 4.7: Eksekusi asynchronous
call.enqueue(new Callback<RestaurantResponse>() {
    // Callback methods
});
```
**Pahami:**
- `Call`: Object yang merepresentasikan 1 API request
- `enqueue()`: Eksekusi di background thread (tidak blocking UI)
- `Callback`: Interface dengan 2 method (onResponse & onFailure)

**Konsep Penting:**
```
Synchronous (BAD ❌):
┌─────┐   ┌──────────┐   ┌──────┐
│ UI  │ → │ Request  │ → │ UI   │
│     │   │ (FREEZE) │   │      │
└─────┘   └──────────┘   └──────┘

Asynchronous (GOOD ✅):
┌─────┐     ┌──────────┐
│ UI  │ →   │ Request  │ (Background)
│     │     │          │
│     │ ←   │ Response │
└─────┘     └──────────┘
(UI tetap responsive)
```

---

**F. STEP 4.8-4.13: Handle Success (onResponse)**
```java
@Override
public void onResponse(Call call, Response response) {
    // STEP 4.9: Cek HTTP status
    if (response.isSuccessful() && response.body() != null) {
        
        // STEP 4.10: Cek error field dari API
        if (!restaurantResponse.isError()) {
            
            // STEP 4.11: Ambil list restaurant
            List<Restaurant> restaurants = ...
            
            // STEP 4.12: Update ArrayList
            restaurantList.clear();
            restaurantList.addAll(restaurants);
            adapter.notifyDataSetChanged();
            
            // STEP 4.13: Show success message
            Toast.makeText(...).show();
        }
    }
}
```

**Pahami setiap check:**
- **response.isSuccessful()**: HTTP status 200-299?
- **response.body() != null**: Ada data?
- **!isError()**: Field error = false?

**Pahami update UI:**
- **clear()**: Hapus data lama
- **addAll()**: Tambah data baru
- **notifyDataSetChanged()**: Trigger refresh ListView

---

**G. STEP 4.14-4.16: Handle Failure (onFailure)**
```java
@Override
public void onFailure(Call call, Throwable t) {
    // STEP 4.15: Log error
    Log.e(TAG, "API Call failed: " + t.getMessage(), t);
    
    // STEP 4.16: Show error to user
    Toast.makeText(...).show();
}
```

**Kapan onFailure dipanggil?**
- Tidak ada koneksi internet
- Timeout
- Server tidak bisa diakses
- Parsing error

---

## 🔄 Flow Eksekusi Lengkap (Timeline)

Ini adalah urutan eksekusi saat aplikasi dijalankan:

```
T=0ms   │ App dibuka
        │
T=10ms  │ STEP 4.1: onCreate() dipanggil
        │
T=15ms  │ STEP 4.2: Setup ListView, Adapter
        │
T=20ms  │ STEP 4.3: apiService = RetrofitClient.getApiService()
        │           └─> Retrofit instance dibuat (STEP 3.3)
        │
T=25ms  │ STEP 4.4: fetchRestaurantList() dipanggil
        │
T=30ms  │ STEP 4.6: Call object dibuat
        │
T=35ms  │ STEP 4.7: call.enqueue() → Request dikirim
        │           └─> Background thread dimulai
        │
        │ ┌─── Background Thread ───┐
        │ │ HTTP GET ke API          │
T=40ms  │ │ Menunggu response...     │
...     │ │                          │
T=500ms │ │ Response diterima!       │
        │ └──────────────────────────┘
        │
T=505ms │ STEP 4.8: onResponse() dipanggil (Main Thread)
        │
T=510ms │ STEP 4.9: Cek response.isSuccessful()
        │
T=515ms │ STEP 4.10: Cek !isError()
        │
T=520ms │ STEP 4.11: Ambil list restaurants
        │
T=525ms │ STEP 4.12: Update ArrayList & notify adapter
        │           └─> ListView refresh!
        │
T=530ms │ STEP 4.13: Toast muncul "Berhasil memuat..."
        │
        │ User melihat list restaurant di layar!
```

---

## 🎯 Checkpoint Pemahaman

Setelah membaca semua STEP, coba jawab pertanyaan ini:

### **Checkpoint STEP 2: Model**
- [ ] Apa fungsi `@SerializedName`?
- [ ] Mengapa perlu class `RestaurantResponse` dan `Restaurant` terpisah?
- [ ] Bagaimana Gson tahu cara mapping JSON ke Object?

### **Checkpoint STEP 3: API**
- [ ] Apa bedanya `ApiService` (interface) dengan `RetrofitClient`?
- [ ] Apa gunanya `HttpLoggingInterceptor`?
- [ ] Mengapa pakai Singleton Pattern di `RetrofitClient`?

### **Checkpoint STEP 4: MainActivity**
- [ ] Apa bedanya `enqueue()` dengan `execute()`?
- [ ] Kapan `onResponse()` dipanggil? Kapan `onFailure()`?
- [ ] Mengapa perlu `adapter.notifyDataSetChanged()`?
- [ ] Kenapa pakai `call.enqueue()` bukan langsung HTTP request?

---

## 🧪 Eksperimen Belajar

### **Level 1: Pemula**
1. Ubah tampilan Toast message
2. Tambah Log.d() di berbagai tempat untuk tracking flow
3. Ganti layout ListView jadi `simple_list_item_2`

### **Level 2: Menengah**
1. Tambahkan ProgressBar yang muncul saat loading
2. Tambahkan onItemClickListener untuk klik restaurant
3. Buat method baru di ApiService untuk endpoint lain

### **Level 3: Lanjut**
1. Ganti ListView jadi RecyclerView
2. Tambahkan gambar restaurant pakai Glide/Picasso
3. Implementasi error handling lebih detail (network error vs API error)
4. Tambahkan SwipeRefreshLayout

---

## 📚 Resource Tambahan

### **Video Tutorial yang Disarankan:**
- Retrofit Basics (Search: "Android Retrofit Tutorial")
- Gson Parsing (Search: "Gson JSON to Object Android")
- ListView vs RecyclerView

### **Dokumentasi:**
- [Retrofit Official Docs](https://square.github.io/retrofit/)
- [Gson User Guide](https://github.com/google/gson/blob/master/UserGuide.md)

---

## ✅ Checklist Belajar

Tandai setelah Anda pahami:

- [ ] **STEP 1**: Paham cara setup dependencies
- [ ] **STEP 2**: Paham konsep Model & JSON mapping
- [ ] **STEP 3**: Paham konfigurasi Retrofit & ApiService
- [ ] **STEP 4.1-4.4**: Paham setup UI & inisialisasi
- [ ] **STEP 4.5-4.7**: Paham cara buat & eksekusi API call
- [ ] **STEP 4.8-4.13**: Paham handle success response
- [ ] **STEP 4.14-4.16**: Paham handle error/failure
- [ ] **BONUS**: Paham flow keseluruhan dari awal sampai akhir

---

## 🎓 Next Learning Path

Setelah paham project ini, lanjutkan ke:

1. **RecyclerView** - List yang lebih advanced
2. **MVVM Architecture** - Pemisahan concern yang lebih baik
3. **Room Database** - Local storage & offline mode
4. **Coroutines** - Alternative untuk async programming
5. **Dependency Injection** - Dagger/Hilt untuk management dependencies

---

**Selamat Belajar! 🚀**

Jangan ragu untuk eksperimen dan break things. Itulah cara terbaik untuk belajar!

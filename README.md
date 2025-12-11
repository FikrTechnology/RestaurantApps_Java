# 🍽️ Restaurant Apps - Dicoding API Integration

Aplikasi Android sederhana untuk menampilkan daftar restaurant menggunakan [Dicoding Restaurant API](https://restaurant-api.dicoding.dev/).

## 📚 Tujuan Pembelajaran

Project ini dibuat untuk belajar:
- ✅ Implementasi Retrofit untuk REST API
- ✅ Parsing JSON dengan Gson
- ✅ Asynchronous programming di Android
- ✅ Menampilkan data di ListView
- ✅ Handling success & error response

---

## 🏗️ Arsitektur Project

```
app/src/main/java/com/example/restaurantapps/
│
├── model/                          # STEP 2: Data Models
│   ├── Restaurant.java            # Model untuk single restaurant
│   └── RestaurantResponse.java    # Model untuk API response
│
├── api/                           # STEP 3: Network Layer
│   ├── ApiService.java            # Interface endpoint API
│   └── RetrofitClient.java        # Retrofit configuration
│
└── MainActivity.java              # STEP 4: UI & Business Logic
```

---

## 🔄 Flow Diagram Lengkap

### **Diagram Alur Aplikasi**

```
┌─────────────────────────────────────────────────────────────────┐
│  STEP 1: PERSIAPAN                                              │
│  ✓ Dependencies: Retrofit, Gson, OkHttp (libs.versions.toml)  │
│  ✓ Permission: INTERNET (AndroidManifest.xml)                  │
└────────────────────────────┬────────────────────────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│  STEP 2: MODEL CLASSES (Data Structure)                        │
│                                                                 │
│  Restaurant.java                                                │
│  ├── id: String                                                 │
│  ├── name: String                                               │
│  ├── description: String                                        │
│  ├── pictureId: String                                          │
│  ├── city: String                                               │
│  └── rating: double                                             │
│                                                                 │
│  RestaurantResponse.java                                        │
│  ├── error: boolean                                             │
│  ├── message: String                                            │
│  ├── count: int                                                 │
│  └── restaurants: List<Restaurant>                              │
└────────────────────────────┬────────────────────────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│  STEP 3: API LAYER (Network Configuration)                     │
│                                                                 │
│  RetrofitClient.java                                            │
│  ├── STEP 3.1: HttpLoggingInterceptor (untuk debugging)        │
│  ├── STEP 3.2: OkHttpClient (HTTP engine)                      │
│  ├── STEP 3.3: Retrofit Builder                                │
│  │   ├── baseUrl: https://restaurant-api.dicoding.dev/         │
│  │   ├── GsonConverterFactory (JSON parser)                    │
│  │   └── OkHttpClient                                           │
│  └── getApiService(): ApiService                                │
│                                                                 │
│  ApiService.java (Interface)                                    │
│  └── @GET("list") → getRestaurantList(): Call<Response>        │
└────────────────────────────┬────────────────────────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│  STEP 4: MAIN ACTIVITY (UI & Business Logic)                   │
│                                                                 │
│  STEP 4.1: onCreate()                                           │
│  └── Dipanggil saat Activity pertama kali dibuat               │
│                                                                 │
│  STEP 4.2: Setup ListView                                       │
│  ├── findViewById(R.id.my_list_view)                            │
│  ├── new ArrayList<Restaurant>()                                │
│  ├── new ArrayAdapter<>()                                       │
│  └── listView.setAdapter(adapter)                               │
│                                                                 │
│  STEP 4.3: Initialize API Service                              │
│  └── apiService = RetrofitClient.getApiService()                │
│                                                                 │
│  STEP 4.4: fetchRestaurantList()                                │
│  └── Panggil method untuk fetch data                            │
└────────────────────────────┬────────────────────────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│  STEP 4.5-4.7: API CALL PROCESS                                │
│                                                                 │
│  STEP 4.6: Buat Call Object                                     │
│  Call<RestaurantResponse> call = apiService.getRestaurantList()│
│                                                                 │
│  STEP 4.7: Eksekusi Asynchronous Request                       │
│  call.enqueue(new Callback<RestaurantResponse>() {...})        │
│                                                                 │
│  ┌──────────────────────────────────────────┐                  │
│  │   NETWORK REQUEST (Background Thread)    │                  │
│  │   GET https://restaurant-api.dicoding.   │                  │
│  │       dev/list                            │                  │
│  └────────────────┬─────────────────────────┘                  │
│                   ▼                                             │
│          ┌────────┴─────────┐                                   │
│          ▼                  ▼                                   │
│     ┌─────────┐      ┌──────────┐                              │
│     │ SUCCESS │      │  FAILURE │                              │
│     └────┬────┘      └─────┬────┘                              │
│          │                 │                                    │
└──────────┼─────────────────┼────────────────────────────────────┘
           ▼                 ▼
    ┌──────────────┐   ┌─────────────┐
    │ onResponse() │   │ onFailure() │
    └──────┬───────┘   └──────┬──────┘
           ▼                  ▼
┌──────────────────┐   ┌────────────────────┐
│ STEP 4.8-4.13:   │   │ STEP 4.14-4.16:    │
│ Handle Success   │   │ Handle Error       │
│                  │   │                    │
│ 4.9: Cek HTTP    │   │ 4.15: Log error    │
│      Status      │   │                    │
│      ↓           │   │ 4.16: Show Toast   │
│ 4.10: Cek error  │   │       error        │
│       field      │   └────────────────────┘
│      ↓           │
│ 4.11: Get list   │
│       restaurants│
│      ↓           │
│ 4.12: Update     │
│       ArrayList  │
│       ├─ clear() │
│       ├─ addAll()│
│       └─ notify  │
│      ↓           │
│ 4.13: Show Toast │
│       success    │
└──────┬───────────┘
       ▼
   ┌────────────────────┐
   │  UI UPDATED        │
   │  ListView shows    │
   │  restaurant data   │
   └────────────────────┘
```

---

## 🔍 Penjelasan Detail Per Step

### **STEP 1: Persiapan Dependencies & Permission**

#### File: `gradle/libs.versions.toml`
```toml
[versions]
retrofit = "2.9.0"
okhttp = "4.12.0"
gson = "2.10.1"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", ... }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", ... }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", ... }
```

**Penjelasan:**
- **Retrofit**: Library untuk HTTP client, mempermudah REST API calls
- **Gson**: Converter untuk parsing JSON ↔ Java Object
- **OkHttp**: HTTP engine & logging untuk debugging

#### File: `app/build.gradle.kts`
```kotlin
dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
}
```

#### File: `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
**Wajib ada** untuk akses internet dari aplikasi!

---

### **STEP 2: Model Classes (Data Structure)**

#### **Restaurant.java**
```java
public class Restaurant {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    // ... dst
}
```

**Fungsi:**
- Blueprint untuk data 1 restaurant
- `@SerializedName`: Mapping otomatis JSON → Java Object oleh Gson

**Contoh Mapping:**
```
JSON:                     Java Object:
{                         Restaurant {
  "id": "abc123",   →       id = "abc123"
  "name": "Cafe",   →       name = "Cafe"
  "rating": 4.5     →       rating = 4.5
}                         }
```

#### **RestaurantResponse.java**
```java
public class RestaurantResponse {
    @SerializedName("restaurants")
    private List<Restaurant> restaurants;
    // ... dst
}
```

**Fungsi:**
- Wrapper untuk keseluruhan API response
- Berisi List<Restaurant> untuk banyak data

---

### **STEP 3: API Layer (Network Configuration)**

#### **RetrofitClient.java**

**STEP 3.1: Logging Interceptor**
```java
HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
```
- Untuk melihat detail request/response di Logcat
- Berguna untuk debugging

**STEP 3.2: OkHttpClient**
```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build();
```
- HTTP engine yang digunakan Retrofit
- Bisa ditambah interceptor, timeout, dll

**STEP 3.3: Retrofit Builder**
```java
retrofit = new Retrofit.Builder()
    .baseUrl("https://restaurant-api.dicoding.dev/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```
- `baseUrl`: URL dasar API
- `client`: OkHttpClient yang sudah dikonfigurasi
- `GsonConverterFactory`: Converter JSON ↔ Object

#### **ApiService.java**
```java
public interface ApiService {
    @GET("list")
    Call<RestaurantResponse> getRestaurantList();
}
```
- Interface yang mendefinisikan endpoint
- `@GET("list")` → HTTP GET ke `/list`
- URL lengkap: `baseUrl + "list"` = `https://restaurant-api.dicoding.dev/list`

---

### **STEP 4: MainActivity (UI & Business Logic)**

#### **STEP 4.1-4.2: Setup UI (onCreate)**
```java
protected void onCreate(Bundle savedInstanceState) {
    // 4.2: Setup ListView
    listView = findViewById(R.id.my_list_view);
    restaurantList = new ArrayList<>();
    adapter = new ArrayAdapter<>(this, 
        android.R.layout.simple_list_item_1, 
        restaurantList);
    listView.setAdapter(adapter);
}
```

**Flow:**
1. Ambil reference ListView dari layout XML
2. Buat ArrayList kosong untuk data
3. Buat Adapter untuk bridge antara data & UI
4. Set adapter ke ListView

#### **STEP 4.3: Initialize API Service**
```java
apiService = RetrofitClient.getApiService();
```
- Dapatkan instance ApiService yang siap pakai
- Singleton pattern memastikan hanya 1 instance

#### **STEP 4.4-4.7: Fetch Data dari API**
```java
private void fetchRestaurantList() {
    // 4.6: Buat Call object
    Call<RestaurantResponse> call = apiService.getRestaurantList();
    
    // 4.7: Eksekusi async request
    call.enqueue(new Callback<RestaurantResponse>() {
        @Override
        public void onResponse(...) { }
        
        @Override
        public void onFailure(...) { }
    });
}
```

**Kenapa Asynchronous?**
```
Synchronous (❌ BURUK):
Request → [WAIT...] → Response → Continue
         (UI FREEZE)

Asynchronous (✅ BAIK):
Request → Continue UI activities
  ↓
[Background] → Response → Update UI
```

#### **STEP 4.8-4.13: Handle Success Response**
```java
public void onResponse(Call call, Response response) {
    // 4.9: Cek HTTP status (200-299 = sukses)
    if (response.isSuccessful() && response.body() != null) {
        
        // 4.10: Cek error field dari API
        if (!restaurantResponse.isError()) {
            
            // 4.11: Ambil list restaurant
            List<Restaurant> restaurants = restaurantResponse.getRestaurants();
            
            // 4.12: Update ArrayList & UI
            restaurantList.clear();              // Hapus data lama
            restaurantList.addAll(restaurants);  // Tambah data baru
            adapter.notifyDataSetChanged();      // Refresh ListView
            
            // 4.13: Tampilkan pesan sukses
            Toast.makeText(...).show();
        }
    }
}
```

#### **STEP 4.14-4.16: Handle Failure**
```java
public void onFailure(Call call, Throwable t) {
    // 4.15: Log error
    Log.e(TAG, "API Call failed: " + t.getMessage(), t);
    
    // 4.16: Tampilkan error ke user
    Toast.makeText(MainActivity.this, 
        "Gagal koneksi: " + t.getMessage(), 
        Toast.LENGTH_SHORT).show();
}
```

**Kapan onFailure dipanggil?**
- Network error (tidak ada internet)
- Timeout
- Parsing error
- Exception lainnya

---

## 🎯 Konsep Penting

### **1. Retrofit Call Flow**

```
ApiService.getRestaurantList()
    ↓
Call<RestaurantResponse> object dibuat
    ↓
call.enqueue(callback)
    ↓
[Background Thread] → HTTP GET ke API
    ↓
Response diterima
    ↓
[Main Thread] → onResponse() atau onFailure()
    ↓
Update UI (safe karena di Main Thread)
```

### **2. Gson Auto-Mapping**

```
JSON Response dari API:
{
  "error": false,
  "message": "success",
  "count": 20,
  "restaurants": [
    {
      "id": "123",
      "name": "Melting Pot",
      "city": "Medan",
      "rating": 4.2
    }
  ]
}

↓ Gson Convert Otomatis ↓

RestaurantResponse {
  error = false
  message = "success"
  count = 20
  restaurants = [
    Restaurant {
      id = "123"
      name = "Melting Pot"
      city = "Medan"
      rating = 4.2
    }
  ]
}
```

### **3. Adapter Pattern**

```
Data (ArrayList) ←→ Adapter ←→ UI (ListView)

- ArrayList: Menyimpan data Restaurant
- Adapter: Bridge yang convert data jadi View
- ListView: Menampilkan View ke user
```

---

## 🚀 Cara Menjalankan

1. **Clone repository**
   ```bash
   git clone <repository-url>
   cd RestaurantApps
   ```

2. **Sync Gradle**
   - Android Studio akan otomatis download dependencies
   - Tunggu sampai sync selesai

3. **Run aplikasi**
   - Klik tombol Run (▶️) di Android Studio
   - Pilih emulator atau device

4. **Lihat log di Logcat**
   - Filter: `MainActivity`
   - Anda akan melihat detail request/response

---

## 📱 Screenshot Output

Aplikasi akan menampilkan list restaurant dengan format:
```
Melting Pot - Medan (4.2⭐)
Kafe Kita - Gorontalo (4.0⭐)
Bring Your Phone Cafe - Medan (4.6⭐)
...
```

---

## 🐛 Troubleshooting

### Error: "Unable to resolve host"
**Penyebab:** Tidak ada koneksi internet
**Solusi:** Pastikan emulator/device terkoneksi internet

### Error: "Permission denied"
**Penyebab:** Lupa tambah INTERNET permission
**Solusi:** Cek `AndroidManifest.xml`, pastikan ada:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Data tidak muncul
**Solusi:**
1. Cek Logcat untuk error message
2. Pastikan API Dicoding masih aktif
3. Cek `HttpLoggingInterceptor` output di Logcat

---

## 📖 Sumber Belajar

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Gson Documentation](https://github.com/google/gson)
- [Dicoding Restaurant API](https://restaurant-api.dicoding.dev/)
- [Android Developers - Connect to Network](https://developer.android.com/training/basics/network-ops/connecting)

---

## 📝 Catatan Pengembangan

### Langkah Selanjutnya (Improvement Ideas):
- [ ] Ganti ListView dengan RecyclerView
- [ ] Tambahkan ProgressBar saat loading
- [ ] Implementasi error handling lebih baik
- [ ] Tambahkan gambar restaurant (Glide/Picasso)
- [ ] Implementasi detail restaurant
- [ ] Tambahkan search & filter
- [ ] Implementasi offline caching (Room Database)

---

## 👨‍💻 Author

Dibuat untuk pembelajaran REST API di Android menggunakan Retrofit & Gson.

---

## 📄 License

Projekt ini dibuat untuk tujuan pembelajaran.

---

**Happy Coding! 🚀**

Jika ada pertanyaan, silakan buka issue atau hubungi saya.

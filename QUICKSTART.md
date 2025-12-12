# Quick Start Guide - Restaurant Apps dengan MVVM

## Setup Cepat (5 Menit)

### 1. Sync Gradle
- File → Sync Now
- Tunggu selesai (dependencies di-download)

### 2. Copy API Base URL
- Base URL: `https://restaurant-api.dicoding.dev/`
- Endpoint: `/list` (return list restaurant sebagai JSON)

### 3. Run Project
- Click Run (Shift + F10)
- Tunggu app install di emulator
- List restaurant akan load otomatis

---

## Project Structure (MVVM)

```
app/src/main/java/com/example/restaurantapps/

├── domain/                      ← Business Logic (Pure Java)
│   ├── model/
│   │   └── Restaurant.java     ← Entity (data bersih)
│   └── repository/
│       └── RestaurantRepository.java  ← Interface (contract)
│
├── data/                        ← Data Sources (API, Database)
│   ├── remote/
│   │   ├── api/
│   │   │   ├── RestaurantApiService.java  ← Retrofit interface
│   │   │   └── RetrofitClient.java        ← HTTP client config
│   │   └── response/
│   │       ├── RestaurantResponse.java    ← API response wrapper
│   │       └── RestaurantDto.java         ← API data mapping
│   └── repository/
│       └── RestaurantRepositoryImpl.java   ← Repository implementation
│
└── presentation/                ← UI (Activity, ViewModel, Adapter)
    └── ui/
        ├── activity/
        │   └── MainActivity.java          ← Activity (UI layer)
        ├── adapter/
        │   └── RestaurantAdapter.java     ← GridView adapter
        └── layout/
            ├── activity_main.xml
            └── grid_item_restaurant.xml
```

### Penjelasan Folder

**Domain (Bisnis Logic)**
- Tidak ada Android dependency
- Entity + Repository interface
- Bisa test tanpa Context

**Data (Fetch Data)**
- Repository implementasi
- DTO mapping (API → Java)
- API client config

**Presentation (UI)**
- Activity (tampilkan data)
- ViewModel (manage state)
- Adapter (grid/list UI)

---

## How It Works - Step by Step

### Flow 1: App Start

```
1. MainActivity.onCreate()
   ↓
2. Create ViewModel instance
   (survives rotation)
   ↓
3. Setup observer untuk LiveData
   ↓
4. viewModel.loadRestaurants()
```

### Flow 2: Load Data

```
viewModel.loadRestaurants()
   ↓
repository.getRestaurants()
   ↓
apiService.getList()  (Retrofit API call)
   ↓
API response RestaurantDto (dari JSON)
   ↓
repository convert DTO → Domain Restaurant
   ↓
callback.onSuccess(restaurants)  ← Return ke ViewModel
   ↓
restaurantList.setValue(restaurants)  ← Update LiveData
   ↓
Activity observer di-notify  ← Automatic!
   ↓
adapter.setData(restaurants)  ← Update UI
```

### Flow 3: Rotate Screen

```
User rotate screen
   ↓
Activity di-destroy
   ↓
Activity di-create lagi (onCreate dipanggil)
   ↓
ViewModel TIDAK di-destroy ✅ (data persist!)
   ↓
viewModel.getRestaurantList() return data yang sama
   ↓
UI update langsung (tidak perlu API call lagi)
```

---

## Key Classes

### Activity (UI Layer)

**File:** `MainActivity.java`

**Tanggung jawab:**
- Initialize UI
- Observe ViewModel data
- Update adapter saat data berubah
- Handle user interactions

**Jangan:**
- ❌ API call langsung
- ❌ Business logic
- ❌ Database query

### ViewModel (State Management)

**File:** `RestaurantViewModel.java`

**Tanggung jawab:**
- Manage data (LiveData)
- Survive configuration changes
- Orchestrate Repository calls
- Handle loading/error state

**Jangan:**
- ❌ Hold Activity reference
- ❌ Update UI directly
- ❌ Long-running task tanpa coroutine

### Repository (Data Abstraction)

**File:** `RestaurantRepositoryImpl.java`

**Tanggung jawab:**
- Fetch data (API / Database)
- Convert DTO → Domain
- Return domain objects
- Handle errors

**Jangan:**
- ❌ Update UI
- ❌ Business logic yang complex
- ❌ Hold Activity reference

---

## Common Interview Questions

### Q1: "Apa itu MVVM?"

**Jawab:** MVVM = Model-View-ViewModel. Pattern untuk separate UI logic (View) dari business logic (Model). ViewModel hold data yang survive rotation.

**Code:**
```java
// ViewModel survive rotation
ViewModel vm = new ViewModelProvider(this).get(RestaurantViewModel.class);
```

### Q2: "Kenapa Repository pattern?"

**Jawab:** Abstraction. Activity tidak tahu apakah data dari API atau Database. Bisa switch tanpa ubah Activity.

**Code:**
```java
// Activity tidak tahu sumber data
repository.getRestaurants(callback);
```

### Q3: "Apa itu LiveData?"

**Jawab:** Observable wrapper untuk data. Saat value berubah, observer di-notify otomatis. Lifecycle-aware (auto un-subscribe).

**Code:**
```java
// Observe dan auto-update
liveData.observe(this, newValue -> updateUI());
```

### Q4: "Kenapa DTO terpisah dari Entity?"

**Jawab:** Flexibility. API bisa berubah tanpa affect domain entity. Plus bisa add logic saat konversi.

**Code:**
```java
// API change hanya affect DTO, bukan Restaurant entity
RestaurantDto dto = response.getBody();
Restaurant entity = convertDtoToEntity(dto);
```

### Q5: "Bagaimana handle rotation?"

**Jawab:** ViewModel tidak di-destroy saat rotation, jadi data persist. Activity di-create lagi tapi ViewModel sama.

**Code:**
```java
// ViewModel survive rotation
@Override
public void onCreate(Bundle savedInstanceState) {
    viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
    // viewModel persist dari rotation sebelumnya
}
```

---

## Debugging Tips

### 1. Lihat API response
- Setup logging di Retrofit (sudah ada di RetrofitClient.java)
- Check Logcat: `OkHttp` tag
- Lihat JSON response dari API

### 2. ViewModel data kosong
```
✓ Check: viewModel.loadRestaurants() sudah dipanggil?
✓ Check: Observer setup dengan benar?
✓ Check: API response ada isi?
```

### 3. UI tidak update
```
✓ Check: Observer di-setup?
✓ Check: LiveData.setValue() sudah dipanggil?
✓ Check: Adapter.notifyDataSetChanged() ada?
```

### 4. API error
```
Check Logcat:
- 404: Endpoint salah
- 500: Server error
- Connection error: Internet problem
```

---

## Next Steps

1. **Understand folder structure**: Pastikan paham domain/data/presentation
2. **Trace code**: Follow flow dari MainActivity → ViewModel → Repository → API
3. **Experiment**:
   - Tambah button untuk reload data
   - Handle error dengan UI (show toast)
   - Add loading spinner
4. **Learn Coroutines**: Replace callback dengan suspend function (lebih clean)
5. **Add Unit Tests**: Test Repository + ViewModel

---

## Useful Links

- [Android MVVM Official](https://developer.android.com/jetpack/guide)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [LiveData Docs](https://developer.android.com/topic/libraries/architecture/livedata)
- [ViewModel Docs](https://developer.android.com/topic/libraries/architecture/viewmodel)

---

*Semua file di dalam project sudah mengikuti MVVM pattern. Telusuri code dan pahami flow data. Happy coding!*

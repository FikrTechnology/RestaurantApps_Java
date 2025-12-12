# MVVM + Clean Architecture Guide untuk Pemula

## Daftar Isi
1. [Apa itu MVVM?](#apa-itu-mvvm)
2. [Clean Architecture Layers](#clean-architecture-layers)
3. [Flow Data dalam MVVM](#flow-data-dalam-mvvm)
4. [Penjelasan Setiap Layer](#penjelasan-setiap-layer)
5. [Interview Q&A](#interview-qa)

---

## Apa itu MVVM?

MVVM = **Model-View-ViewModel**

### Masalah Tanpa MVVM

```java
// ❌ BAD: Semua logic di Activity
public class OldActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // API call langsung di Activity
        Retrofit retrofit = Retrofit.Builder()...build();
        RestaurantApi api = retrofit.create(RestaurantApi.class);
        
        api.getList().enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                // Update UI langsung
                adapter.setData(response.body());
            }
        });
    }
    
    // Masalah:
    // 1. Saat rotate screen, onCreate() dipanggil lagi → API dipanggil lagi
    // 2. Logic tercampur dengan UI logic
    // 3. Sulit testing (tidak bisa test logic tanpa Activity)
}
```

### Solusi Dengan MVVM

```java
// ✅ GOOD: Separation of concerns
public class NewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get ViewModel (survives rotation)
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        
        // Observe data
        viewModel.getRestaurantList().observe(this, restaurants -> {
            adapter.setData(restaurants);
        });
        
        // Load data
        viewModel.loadRestaurants();
    }
    
    // Keuntungan:
    // 1. Activity tidak tahu cara fetch data
    // 2. Data persist saat rotate screen (ViewModel tidak di-destroy)
    // 3. Logic terpisah dari UI (mudah test)
}
```

---

## Clean Architecture Layers

Project kami punya 3 layer:

```
┌─────────────────────────────────────────┐
│   PRESENTATION LAYER (UI)               │
│  - Activity, Fragment, ViewModel        │
│  - Menampilkan data ke user             │
├─────────────────────────────────────────┤
│   DATA LAYER (Repository)               │
│  - API calls, Database queries          │
│  - Convert DTO → Domain model           │
├─────────────────────────────────────────┤
│   DOMAIN LAYER (Business Logic)         │
│  - Entity, Repository interface         │
│  - Pure Java (no Android dependency)    │
└─────────────────────────────────────────┘
```

### Keuntungan Layer Separation

1. **Testability**: Bisa test repository tanpa Activity
2. **Maintainability**: Perubahan API tidak affect Activity
3. **Reusability**: Satu repository bisa dipakai multiple Activity
4. **Scalability**: Mudah tambah feature baru

---

## Flow Data dalam MVVM

### Urutan Event Step-by-Step

```
1. Activity.onCreate()
   ↓
2. ViewModelProvider.get(RestaurantViewModel.class)
   ↓
3. viewModel.observe(restaurantList)
   ↓
4. viewModel.loadRestaurants()
   ↓
5. ViewModel.loadRestaurants() → call Repository
   ↓
6. Repository.getRestaurants() → call API
   ↓
7. API respond dengan JSON
   ↓
8. Repository convert DTO → Domain Restaurant
   ↓
9. Repository callback.onSuccess(restaurants)
   ↓
10. ViewModel.restaurantList.setValue(restaurants)  ← Update LiveData!
    ↓
11. Activity observer di-notify otomatis
    ↓
12. Activity update UI (adapter.setData())
```

### Code Flow Example

**Activity:**
```java
viewModel.loadRestaurants(); // Call ViewModel
```

**ViewModel:**
```java
public void loadRestaurants() {
    repository.getRestaurants(callback);
}
```

**Repository:**
```java
public void getRestaurants(callback) {
    apiService.getList().enqueue(new Callback<RestaurantResponse>() {
        onResponse(...) {
            List<Restaurant> restaurants = convertDtoToDomain(response.getDtos());
            callback.onSuccess(restaurants); // Return ke ViewModel
        }
    });
}
```

**ViewModel (lanjutan):**
```java
callback.onSuccess(restaurants) {
    restaurantList.setValue(restaurants); // Update LiveData
}
```

**Activity (observer dipanggil):**
```java
restaurantList.observe(this, restaurants -> {
    adapter.setData(restaurants); // Update UI
});
```

---

## Penjelasan Setiap Layer

### 1. DOMAIN LAYER (Logika Bisnis)

**Lokasi:** `domain/`

**File penting:**
- `domain/model/Restaurant.java` - Entity (data bersih)
- `domain/repository/RestaurantRepository.java` - Interface

**Ciri-ciri:**
- **Tidak ada Android dependency** (no Context, no View, no Activity)
- **Pure Java** - bisa dipakai di project non-Android juga
- **Interface terlebih dahulu** - baru implementasi

**Contoh:**
```java
// ✅ GOOD - Pure Java entity
public class Restaurant {
    private String id, name, description, pictureId, city;
    private double rating;
    
    // getters/setters
}

// ✅ GOOD - Interface, implementasi di data layer
public interface RestaurantRepository {
    void getRestaurants(RestaurantCallback callback);
}
```

**Interview Q:** "Kenapa domain layer tidak boleh ada Android dependency?"

**Jawab:** Supaya mudah testing dan reusable. Bisa test Restaurant entity tanpa Context. Bisa juga pindah ke project backend nantinya.

---

### 2. DATA LAYER (Fetch Data)

**Lokasi:** `data/`

**Sub-folder:**
- `data/remote/api/` - Retrofit interfaces dan API client
- `data/remote/response/` - DTO (API response mapping)
- `data/repository/` - Repository implementation

**Ciri-ciri:**
- **Punya Android dependency** (OK, karena fetch data)
- **DTO (Data Transfer Object)** - mapping JSON → Java object
- **Repository implementasi** - dari domain interface

**Contoh:**

```java
// Step 1: Retrofit Interface (API contract)
@GET("list")
Call<RestaurantResponse> getList();

// Step 2: DTO (JSON mapping)
public class RestaurantDto {
    @SerializedName("id")
    private String id;
    // Other fields...
}

// Step 3: Repository (implementasi interface domain)
public class RestaurantRepositoryImpl implements RestaurantRepository {
    public void getRestaurants(callback) {
        apiService.getList().enqueue(new Callback<RestaurantResponse>() {
            onResponse(...) {
                // Convert DTO → Restaurant
                List<Restaurant> restaurants = convertDtoToDomain(response.getDtos());
                callback.onSuccess(restaurants);
            }
        });
    }
}
```

**Why DTO?** 

Jika API response berubah:
- Tanpa DTO: Domain Restaurant juga berubah → Activity juga berubah
- Dengan DTO: Hanya DTO berubah → Repository konversi → domain Restaurant tetap

```
Tanpa DTO:
API change → Restaurant change → Activity change → Risky!

Dengan DTO:
API change → RestaurantDto change → Repository handle → Restaurant tetap → Activity tetap ✅
```

**Interview Q:** "Kenapa perlu DTO jika bisa langsung mapping API ke Restaurant?"

**Jawab:** Flexibility. API bisa berubah (field nama, type, dll). DTO melindungi domain dari perubahan API. Plus, bisa add business logic saat konversi (filter, validation, dll).

---

### 3. PRESENTATION LAYER (UI)

**Lokasi:** `presentation/`

**Sub-folder:**
- `presentation/viewmodel/` - ViewModel (state management)
- `presentation/ui/activity/` - Activity
- `presentation/ui/adapter/` - RecyclerView/GridView adapter
- `presentation/ui/layout/` - XML layout files

**Ciri-ciri:**
- **Activity adalah dumb view** - hanya menampilkan, tidak logika
- **ViewModel hold data** - survive rotation
- **Observe LiveData** - reactive update

**Contoh:**

```java
// Activity (dumb view)
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        
        // Observe and update UI
        viewModel.getRestaurantList().observe(this, restaurants -> {
            adapter.setData(restaurants);
        });
        
        // That's it! Activity hanya observe, tidak logic
    }
}

// ViewModel (hold data + logic)
public class RestaurantViewModel extends ViewModel {
    private MutableLiveData<List<Restaurant>> restaurantList = new MutableLiveData<>();
    
    public void loadRestaurants() {
        repository.getRestaurants(new Callback() {
            onSuccess(restaurants) {
                restaurantList.setValue(restaurants); // Update LiveData
            }
        });
    }
}
```

**Interview Q:** "Kenapa ViewModel tidak di-destroy saat rotate screen?"

**Jawab:** ViewModel lifecycle independent dari Activity. Activity di-destroy saat rotate, tapi ViewModel tetap di-memory. Jadi data tidak hilang, dan API tidak dipanggil lagi.

---

## Interview Q&A

### Q1: "Apa itu LiveData dan kenapa penting?"

**Jawab:**
LiveData adalah **observable wrapper** untuk data. Saat value berubah, semua observer di-notify otomatis.

```java
// Tanpa LiveData (polling)
while (true) {
    if (data_changed) {
        updateUI();
    }
    Thread.sleep(1000); // Check setiap 1 detik - boros battery!
}

// Dengan LiveData (reactive)
liveData.observe(this, newData -> {
    updateUI(); // Langsung dipanggil saat data berubah!
});
```

**Keuntungan:**
- **Efficient**: Tidak perlu polling, dipanggil saat ada change
- **Lifecycle-aware**: Observer otomatis un-subscribe saat Activity destroy
- **Thread-safe**: Aman di-access dari multiple threads

---

### Q2: "Apa bedanya ViewModel vs Singleton?"

**Jawab:**
```java
// Singleton (salah untuk Activity)
public class DataManager {
    private static DataManager instance = new DataManager();
    private List<Restaurant> restaurants;
    
    public static DataManager getInstance() {
        return instance;
    }
}

// ViewModel (benar untuk Activity)
public class RestaurantViewModel extends ViewModel {
    private MutableLiveData<List<Restaurant>> restaurants;
}
```

**Perbedaan:**
- **Singleton**: Hidup selama app berjalan. Jika reference tidak di-clear, memory leak!
- **ViewModel**: Hidup selama Activity/Fragment. Otomatis di-clear saat destroy.

**Interview point:** "ViewModel adalah design pattern khusus untuk Activity lifecycle. Singleton tidak aman untuk Activity."

---

### Q3: "Bagaimana cara test ViewModel?"

**Jawab:**
```java
// Test tanpa Activity atau API
@Test
public void testLoadRestaurants() {
    // Mock repository
    RestaurantRepository mockRepo = mock(RestaurantRepository.class);
    
    // Inject ke ViewModel
    RestaurantViewModel viewModel = new RestaurantViewModel(mockRepo);
    
    // Trigger
    viewModel.loadRestaurants();
    
    // Verify
    verify(mockRepo).getRestaurants(callback);
}
```

**Keuntungan MVVM:**
- ViewModel tidak tergantung Activity/Context → Mudah mock
- Repository interface → Bisa pass mock implementation
- Business logic terpisah dari UI → Testable

---

### Q4: "Kenapa perlu Repository pattern?"

**Jawab:**
```java
// Tanpa Repository (Activity tahu semua data source)
public class Activity {
    void loadData() {
        if (hasInternet) {
            apiService.getList(); // API
        } else {
            database.getList(); // Database
        }
    }
}

// Dengan Repository (Activity tidak tahu)
public class Activity {
    void loadData() {
        repository.getList(); // Repository decide sumber data
    }
}
```

**Keuntungan:**
- **Abstraction**: Activity tidak perlu tahu cara fetch data
- **Flexibility**: Switch API → Database tanpa ubah Activity
- **Testability**: Bisa mock repository

---

### Q5: "Apakah kita perlu UseCase layer juga?"

**Jawab:**
UseCase adalah optional, untuk project besar dengan banyak business logic.

```java
// Dengan UseCase (recommended untuk project besar)
public class GetRestaurantsUseCase {
    private RestaurantRepository repository;
    
    public void execute(Callback callback) {
        // Business logic: filter, sort, validate, dll
        List<Restaurant> all = repository.getRestaurants();
        List<Restaurant> filtered = all.filter(r -> r.rating > 4.0);
        callback.onSuccess(filtered);
    }
}

// Tanpa UseCase (OK untuk project kecil)
public class ViewModel {
    public void loadRestaurants() {
        repository.getRestaurants(callback);
    }
}
```

**Saat pakai UseCase:**
- Banyak business logic (filter, sort, transform)
- Business logic shared di multiple ViewModel
- Project besar

**Saat tidak perlu UseCase:**
- Project kecil/sedang
- Business logic simple
- Tidak perlu reuse logic

---

## Summary

### MVVM Flow Simple

```
1. Activity request ViewModel
2. ViewModel request Repository
3. Repository request API
4. API response → Repository convert DTO → Domain
5. Repository return domain → ViewModel
6. ViewModel update LiveData
7. Activity observer di-notify
8. Activity update UI
```

### Analogi Dunia Nyata

**Tanpa MVVM (Chaotic)**
```
Customer (Activity) → Langsung ke warehouse (API)
- Harus tau warehouse location
- Harus tau inventory system
- Kalo warehouse move, customer juga kebingungan
```

**Dengan MVVM (Organized)**
```
Customer (Activity) → Receptionist (ViewModel) → Warehouse (Repository) → Supplier (API)
- Customer tidak perlu tahu warehouse
- Receptionist handle semua
- Kalo warehouse move, receptionist adjust, customer tidak perlu tau
```

---

## Next Steps

1. **Pahami folder structure** di project
2. **Trace code flow** dari Activity → ViewModel → Repository → API
3. **Experiment**: 
   - Rotate screen → Lihat ViewModel survive
   - Change API response → Lihat DTO berubah, Activity tetap
4. **Add UseCase** jika business logic complex
5. **Add testing** untuk Repository dan ViewModel

---

*Setiap layer punya tanggung jawab. Jangan campur-aduk. Separation of concerns = sustainable code.*

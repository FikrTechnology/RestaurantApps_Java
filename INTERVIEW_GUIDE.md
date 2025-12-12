# MVVM Interview Questions & Answers

Complete guide with code examples untuk interview preparation.

---

## 1. MVVM Architecture

### Q: "Apa itu MVVM dan bagaimana cara kerjanya?"

**Jawab:**

MVVM = **Model-View-ViewModel**. Pattern untuk separate concerns:

- **Model**: Data + Business logic (Repository)
- **View**: UI components (Activity, Fragment)
- **ViewModel**: State management + orchestrate data

**Cara kerja:**
```java
View (Activity)
   ↓ (observes)
ViewModel (hold data in LiveData)
   ↓ (use)
Repository (fetch from API/DB)
   ↓ (fetch from)
API / Database
```

**Keuntungan MVVM:**
- Separation of concerns
- Testable (bisa test ViewModel tanpa Activity)
- Lifecycle-aware
- Survive configuration changes (rotation)

---

### Q: "Apa bedanya MVVM vs MVC vs MVP?"

**Jawab:**

| Aspect | MVC | MVP | MVVM |
|--------|-----|-----|------|
| **Controller/Presenter/ViewModel** | Hold logic | Hold logic + interface | Hold state + logic |
| **View** | Update UI | Update UI (dumb) | Observe ViewModel |
| **Testable** | Sulit | Medium | Mudah |
| **Android fit** | Tidak | Ok | Bagus (lifecycle-aware) |
| **Data binding** | Manual | Manual | Auto (LiveData) |

**Konteks Android:**

```java
// ❌ MVC - Controller di Activity (tidak cocok Android)
public class MainActivity extends AppCompatActivity {
    public void onCreate() {
        // Logic tercampur UI
        apiCall().then(data -> updateUI(data));
    }
}

// ❌ MVP - Presenter hold interface (verbose)
public class MainActivity extends AppCompatActivity implements RestaurantView {
    private RestaurantPresenter presenter;
    
    public void onCreate() {
        presenter = new RestaurantPresenter(this);
        presenter.loadRestaurants();
    }
    
    @Override
    public void showRestaurants(List<Restaurant> data) {
        // Presenter call view method
        adapter.setData(data);
    }
}

// ✅ MVVM - ViewModel hold LiveData (clean + auto update)
public class MainActivity extends AppCompatActivity {
    private RestaurantViewModel viewModel;
    
    public void onCreate() {
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        
        // Observe LiveData
        viewModel.getRestaurantList().observe(this, data -> {
            adapter.setData(data);
        });
        
        // Load data
        viewModel.loadRestaurants();
    }
}
```

---

## 2. ViewModel & LiveData

### Q: "Apa itu ViewModel dan kenapa important?"

**Jawab:**

ViewModel adalah container untuk UI state yang survive configuration changes.

**Masalah tanpa ViewModel:**

```java
public class OldActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Ini dipanggil lagi saat rotate screen!
        loadDataFromAPI(); // ← API call lagi = data loss + wasted request
    }
    
    private void loadDataFromAPI() {
        apiService.getList().enqueue(callback);
    }
}
```

**Solusi dengan ViewModel:**

```java
public class NewActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // ViewModel survive rotation (tidak di-destroy)
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        
        // Data persist dari sebelum rotation
        viewModel.getRestaurantList().observe(this, restaurants -> {
            adapter.setData(restaurants);
        });
        
        // Hanya load pertama kali jika belum ada data
        if (viewModel.restaurantList.getValue() == null) {
            viewModel.loadRestaurants();
        }
    }
}
```

**Keuntungan:**
- Data persist across rotation
- Tidak perlu re-fetch API
- Lifecycle-aware (auto cleanup)

---

### Q: "Apa itu LiveData dan kenapa penting?"

**Jawab:**

LiveData adalah **observable wrapper** untuk data yang lifecycle-aware.

**Tanpa LiveData (polling):**

```java
// ❌ BAD: Manual polling (boros battery)
Thread thread = new Thread(() -> {
    while (true) {
        if (dataChanged()) {
            updateUI();
        }
        Thread.sleep(1000); // Check setiap 1 detik
    }
});
```

**Dengan LiveData (reactive):**

```java
// ✅ GOOD: Auto-notify saat value berubah
liveData.observe(this, newData -> {
    updateUI(newData); // Dipanggil LANGSUNG saat value berubah
});
```

**Keuntungan LiveData:**
- **Reactive**: Dipanggil saat ada change (tidak polling)
- **Lifecycle-aware**: Auto un-subscribe saat Activity destroy
- **Thread-safe**: Aman di-access dari multiple threads
- **No memory leak**: Activity reference di-clear otomatis

---

### Q: "Kapan create ViewModel dan kapan destroy?"

**Jawab:**

```java
// CREATE: Saat Activity pertama kali dibuat
public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // ViewModel created di sini (atau reuse jika sudah ada)
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
    }
}

// LIFECYCLE:
// Activity.onCreate() → ViewModel created
// (Activity rotate) → Activity destroyed + recreated
//                  → ViewModel SURVIVE (not destroyed)
// Activity.onDestroy() (final) → ViewModel destroyed

// Diagram:
// Activity: |---onCreate---|rotate|---onCreate---|onDestroy|
// ViewModel: |---created----|saved|----|destroyed|
```

---

## 3. Repository Pattern

### Q: "Apa itu Repository pattern dan kenapa penting?"

**Jawab:**

Repository adalah abstraction layer antara domain dan data sources.

**Tanpa Repository:**

```java
// ❌ BAD: Activity tahu semua source data
public class MainActivity extends AppCompatActivity {
    public void loadData() {
        if (hasInternet()) {
            // Activity tahu API
            apiService.getList().enqueue(callback);
        } else if (hasCachedData()) {
            // Activity tahu Database
            database.getList();
        } else {
            // Activity tahu file
            fileManager.readRestaurants();
        }
    }
}
```

**Dengan Repository:**

```java
// ✅ GOOD: Activity hanya tahu Repository
public class MainActivity extends AppCompatActivity {
    public void loadData() {
        // Repository decide sumber data
        repository.getRestaurants(callback);
    }
}

// Repository implementasi:
public class RestaurantRepositoryImpl implements RestaurantRepository {
    @Override
    public void getRestaurants(Callback callback) {
        // Repository decide strategy
        if (hasInternet()) {
            getFromAPI(callback);
        } else if (hasCachedData()) {
            getFromDatabase(callback);
        } else {
            getFromFile(callback);
        }
    }
}
```

**Keuntungan:**
- **Abstraction**: Activity tidak tahu sumber data
- **Flexibility**: Bisa switch API → Database tanpa ubah Activity
- **Testability**: Bisa mock repository
- **Single Responsibility**: Repository hanya handle data

---

### Q: "Bagaimana Repository handle error?"

**Jawab:**

```java
public class RestaurantRepositoryImpl implements RestaurantRepository {
    @Override
    public void getRestaurants(RestaurantCallback callback) {
        apiService.getList().enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                // ✓ Check HTTP status
                if (!response.isSuccessful()) {
                    callback.onError("HTTP error: " + response.code());
                    return;
                }
                
                // ✓ Check body null
                if (response.body() == null) {
                    callback.onError("Response body is null");
                    return;
                }
                
                // ✓ Check API error field
                if (response.body().isError()) {
                    callback.onError(response.body().getMessage());
                    return;
                }
                
                // ✓ Safe to proceed
                List<RestaurantDto> dtos = response.body().getRestaurants();
                List<Restaurant> restaurants = convertDtoToDomain(dtos);
                callback.onSuccess(restaurants);
            }
            
            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                // ✓ Network error
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
```

---

## 4. DTO vs Entity

### Q: "Kenapa DTO terpisah dari Entity? Tidak bisa langsung ke Entity?"

**Jawab:**

DTO = API response structure, Entity = business model. Mereka berbeda!

**Tanpa DTO (mencampur):**

```java
// ❌ BAD: Langsung gunakan satu class
@SerializedName("picture_id")
private String pictureId;

public class Restaurant {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    // ... API annotations di Entity = tight coupling
}

// Masalah: API berubah → Entity berubah → Activity berubah
```

**Dengan DTO:**

```java
// ✅ GOOD: Terpisah DTO dan Entity

// Data Transfer Object (API layer)
public class RestaurantDto {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    // Hanya berisi API annotations
}

// Domain Entity (business layer)
public class Restaurant {
    private String id;
    private String name;
    // Pure Java, no API dependency
}

// Konversi di Repository:
private List<Restaurant> convertDtoToDomain(List<RestaurantDto> dtos) {
    List<Restaurant> entities = new ArrayList<>();
    for (RestaurantDto dto : dtos) {
        Restaurant entity = new Restaurant();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        // ... mapping
        entities.add(entity);
    }
    return entities;
}
```

**Flow dengan API change:**

```
Scenario: API ubah field "name" jadi "title"

Tanpa DTO:
API change → Update Restaurant class @SerializedName
          → Update ViewModel (if logic need update)
          → Update Activity (if UI need update)
          → RISKY! Banyak file berubah

Dengan DTO:
API change → Update RestaurantDto @SerializedName
          → Update convertDtoToDomain() logic
          → Repository handle conversion
          → Activity & ViewModel TIDAK perlu ubah ✅
          → SAFE! Hanya data layer affected
```

---

## 5. Data Flow & Testing

### Q: "Trace complete data flow dari Activity click sampai UI update."

**Jawab:**

```
1. User interact Activity
   ↓
   findViewById(R.id.buttonLoad).setOnClickListener(v -> {
       viewModel.loadRestaurants();  ← Call ViewModel
   });

2. ViewModel.loadRestaurants()
   ↓
   isLoading.setValue(true); ← Notify loading state
   repository.getRestaurants(new Callback {
       onSuccess(restaurants) { ... }
       onError(error) { ... }
   });

3. Repository.getRestaurants()
   ↓
   apiService.getList().enqueue(new Callback {
       onResponse(...) {
           // Convert DTO → Entity
           List<Restaurant> entities = convertDtoToDomain(dtos);
           callback.onSuccess(entities);
       }
       onFailure(...) {
           callback.onError(error);
       }
   });

4. API response dipanggil dari network thread
   ↓
   Restaurant API return JSON
   Retrofit map JSON → RestaurantDto

5. Repository callback dipanggil (callback.onSuccess)
   ↓
   ViewModel onSuccess(restaurants)
   restaurantList.setValue(restaurants); ← Update LiveData on main thread

6. Activity observer di-notify
   ↓
   restaurantList.observe(this, restaurants -> {
       adapter.setData(restaurants);
       adapter.notifyDataSetChanged();
       isLoading.setValue(false);
   });

7. GridView re-render
   ↓
   getView() dipanggil untuk setiap cell
   ViewHolder bind data ke UI components
   Images load via Glide
   Restaurant items tampil di grid
```

---

### Q: "Bagaimana cara test ViewModel tanpa Activity?"

**Jawab:**

```java
// JUnit test (no Android dependency)
@RunWith(JUnit4.class)
public class RestaurantViewModelTest {
    
    private RestaurantViewModel viewModel;
    private RestaurantRepository mockRepository;
    
    @Before
    public void setUp() {
        // Mock repository
        mockRepository = mock(RestaurantRepository.class);
        
        // Create ViewModel with mock
        viewModel = new RestaurantViewModel(mockRepository);
    }
    
    @Test
    public void testLoadRestaurants_Success() {
        // Arrange: Prepare mock data
        List<Restaurant> expectedData = Arrays.asList(
            new Restaurant("1", "Warung Nasi", "Jakarta", 4.5),
            new Restaurant("2", "Soto Ayam", "Bandung", 4.2)
        );
        
        // Act: Trigger load
        viewModel.loadRestaurants();
        
        // Mock callback
        ArgumentCaptor<RestaurantRepository.RestaurantCallback> captor = 
            ArgumentCaptor.forClass(RestaurantRepository.RestaurantCallback.class);
        
        verify(mockRepository).getRestaurants(captor.capture());
        
        // Simulate success response
        captor.getValue().onSuccess(expectedData);
        
        // Assert: Verify LiveData updated
        assertEquals(expectedData, viewModel.getRestaurantList().getValue());
        assertTrue(viewModel.getIsLoading().getValue() == false);
    }
    
    @Test
    public void testLoadRestaurants_Error() {
        // Arrange
        String expectedError = "Network error";
        
        // Act
        viewModel.loadRestaurants();
        
        // Capture and trigger error
        ArgumentCaptor<RestaurantRepository.RestaurantCallback> captor = 
            ArgumentCaptor.forClass(RestaurantRepository.RestaurantCallback.class);
        
        verify(mockRepository).getRestaurants(captor.capture());
        captor.getValue().onError(expectedError);
        
        // Assert
        assertEquals(expectedError, viewModel.getErrorMessage().getValue());
        assertTrue(viewModel.getIsLoading().getValue() == false);
    }
}
```

---

## 6. Configuration Changes

### Q: "Apa yang terjadi saat user rotate screen?"

**Jawab:**

```
SEBELUM ROTATION:
┌─────────────────────┐
│ Activity.onCreate() │
│   ↓                 │
│ ViewModel created   │
│   ↓                 │
│ Load data from API  │
│   ↓                 │
│ Show restaurants    │
└─────────────────────┘

USER ROTATE SCREEN:

Activity destroyed    ← System call Activity.onDestroy()
Activity recreated    ← System call Activity.onCreate() lagi
  ↓
ViewModelProvider.get(ViewModel.class)
  → Check: ViewModel sudah ada? YES ✅
  → Return existing ViewModel (TIDAK create baru)
  ↓
ViewModel restored dengan data lama
  ↓
LiveData observer di-setup lagi
  ↓
Observer di-notify dengan value yang sama
  ↓
UI update langsung (tanpa API call)
```

**Code:**

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // PENTING: Gunakan ViewModelProvider, jangan create manual
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        
        // ↑ Ini smart:
        // - First time: create ViewModel baru
        // - After rotation: return ViewModel existing
        
        viewModel.getRestaurantList().observe(this, restaurants -> {
            adapter.setData(restaurants);
        });
        
        // Hanya load data jika belum ada (first time atau after clear app data)
        if (viewModel.getRestaurantList().getValue() == null) {
            viewModel.loadRestaurants();
        }
    }
}
```

---

## 7. Common Mistakes

### Q: "Apa kesalahan umum dalam MVVM?"

**Jawab:**

**❌ Mistake 1: Create ViewModel manually**

```java
// WRONG
public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        viewModel = new RestaurantViewModel(); // ❌ Wrong!
        // Setiap onCreate(), ViewModel baru di-create
        // Data hilang saat rotation
    }
}

// CORRECT
public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class); // ✅
        // ViewModel reuse dari cache
    }
}
```

**❌ Mistake 2: Activity hold Context di ViewModel**

```java
// WRONG
public class RestaurantViewModel extends ViewModel {
    private Context context; // ❌ MEMORY LEAK!
    
    public RestaurantViewModel(Context context) {
        this.context = context; // Activity reference
    }
    
    // Saat Activity destroy, ViewModel still hold reference
    // GC tidak bisa cleanup Activity → memory leak
}

// CORRECT
public class RestaurantViewModel extends ViewModel {
    // Tidak hold Activity reference
    // Jika perlu context, use AndroidViewModel
}

public class RestaurantViewModel extends AndroidViewModel {
    private Application app; // ✅ Safe (app context, not activity)
    
    public RestaurantViewModel(Application app) {
        super(app);
        this.app = app;
    }
}
```

**❌ Mistake 3: Not handling null in LiveData observer**

```java
// WRONG
viewModel.getRestaurantList().observe(this, restaurants -> {
    adapter.setData(restaurants); // ❌ restaurants bisa null!
    for (Restaurant r : restaurants) { // NullPointerException
        // ...
    }
});

// CORRECT
viewModel.getRestaurantList().observe(this, restaurants -> {
    if (restaurants != null && !restaurants.isEmpty()) {
        adapter.setData(restaurants); // ✅
    } else {
        showEmptyState();
    }
});
```

**❌ Mistake 4: Not unsubscribe from LiveData**

```java
// WRONG - tidak relevant untuk LiveData sebenarnya
// LiveData auto un-subscribe saat Activity destroy
// Tapi tetap good practice untuk explicit cleanup jika perlu

// CORRECT
@Override
protected void onDestroy() {
    super.onDestroy();
    // LiveData otomatis un-subscribe (lifecycle-aware)
}
```

---

## 8. Performance & Best Practices

### Q: "Bagaimana optimize ViewModel untuk performance?"

**Jawab:**

```java
// ❌ BAD: Load data di setiap onCreate
public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        viewModel.loadRestaurants(); // ❌ Called setiap rotation!
    }
}

// ✅ GOOD: Load hanya sekali
public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (viewModel.getRestaurantList().getValue() == null) {
            viewModel.loadRestaurants(); // ✅ Only if data kosong
        }
    }
}
```

```java
// ✅ GOOD: Use MutableLiveData untuk different states
public class RestaurantViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<List<Restaurant>> restaurantList = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // Benefits:
    // - Granular updates (loading state independent dari data)
    // - Observer can react to specific changes
    // - UI responsive
}
```

---

## Summary Checklist

### Interview Preparation Checklist

- [ ] Understand MVVM pattern (Model-View-ViewModel)
- [ ] Know ViewModel lifecycle (create, survive rotation, destroy)
- [ ] Know LiveData benefits (reactive, lifecycle-aware)
- [ ] Understand Repository pattern (abstraction, testability)
- [ ] Know DTO vs Entity (flexibility, API change handling)
- [ ] Trace complete data flow (Activity → ViewModel → Repository → API)
- [ ] Know how to test ViewModel (mocking)
- [ ] Know common mistakes (create ViewModel manually, context leak, null checks)
- [ ] Can explain why MVVM > MVC/MVP untuk Android

### Code You Should Know

- ViewModelProvider.get() (how to create/get ViewModel)
- LiveData.observe() (how to observe)
- MutableLiveData.setValue() (how to update)
- Repository pattern (abstraction)
- DTO to Entity conversion
- Error handling (onFailure, response check)

---

*Study code dalam project. Pahami setiap layer. Trace flow. Good luck interview!*

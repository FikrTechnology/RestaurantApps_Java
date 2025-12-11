# 🔧 API Documentation

Dokumentasi lengkap untuk Restaurant API dari Dicoding.

---

## 📡 Base URL

```
https://restaurant-api.dicoding.dev/
```

---

## 🍽️ Endpoints

### **1. Get Restaurant List**

Mendapatkan daftar semua restaurant yang tersedia.

**Endpoint:**
```
GET /list
```

**Full URL:**
```
https://restaurant-api.dicoding.dev/list
```

**Request Headers:**
```
Content-Type: application/json
```

**Request Parameters:**
```
Tidak ada parameter
```

**Response Success (200 OK):**

```json
{
  "error": false,
  "message": "success",
  "count": 20,
  "restaurants": [
    {
      "id": "rqdv5juczeskfw1e867",
      "name": "Melting Pot",
      "description": "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. ...",
      "pictureId": "14",
      "city": "Medan",
      "rating": 4.2
    },
    {
      "id": "s1knt6za9kkfw1e867",
      "name": "Kafe Kita",
      "description": "Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. ...",
      "pictureId": "25",
      "city": "Gorontalo",
      "rating": 4
    }
  ]
}
```

**Response Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `error` | boolean | Status error. `false` = sukses, `true` = error |
| `message` | string | Pesan dari server |
| `count` | integer | Jumlah restaurant yang dikembalikan |
| `restaurants` | array | Array berisi object restaurant |

**Restaurant Object Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `id` | string | ID unik restaurant |
| `name` | string | Nama restaurant |
| `description` | string | Deskripsi restaurant |
| `pictureId` | string | ID gambar restaurant |
| `city` | string | Kota lokasi restaurant |
| `rating` | number | Rating restaurant (0-5) |

**Picture URL Format:**

Untuk mendapatkan gambar restaurant, gunakan format:

- **Small:** `https://restaurant-api.dicoding.dev/images/small/{pictureId}`
- **Medium:** `https://restaurant-api.dicoding.dev/images/medium/{pictureId}`
- **Large:** `https://restaurant-api.dicoding.dev/images/large/{pictureId}`

Contoh:
```
https://restaurant-api.dicoding.dev/images/medium/14
```

---

## 📝 Response Examples

### **Success Response Example:**

```json
{
  "error": false,
  "message": "success",
  "count": 3,
  "restaurants": [
    {
      "id": "rqdv5juczeskfw1e867",
      "name": "Melting Pot",
      "description": "Lorem ipsum dolor sit amet...",
      "pictureId": "14",
      "city": "Medan",
      "rating": 4.2
    },
    {
      "id": "s1knt6za9kkfw1e867",
      "name": "Kafe Kita",
      "description": "Quisque rutrum. Aenean imperdiet...",
      "pictureId": "25",
      "city": "Gorontalo",
      "rating": 4
    },
    {
      "id": "w9pga3s2tubkfw1e867",
      "name": "Bring Your Phone Cafe",
      "description": "Maecenas ullamcorper, dui et placerat...",
      "pictureId": "03",
      "city": "Medan",
      "rating": 4.6
    }
  ]
}
```

### **Error Response Example:**

```json
{
  "error": true,
  "message": "Restaurant not found"
}
```

---

## 🧪 Testing dengan cURL

**Command:**
```bash
curl -X GET "https://restaurant-api.dicoding.dev/list" \
     -H "Content-Type: application/json"
```

**Expected Output:**
```json
{
  "error": false,
  "message": "success",
  "count": 20,
  "restaurants": [...]
}
```

---

## 🔍 Testing dengan Postman

1. **Method:** GET
2. **URL:** `https://restaurant-api.dicoding.dev/list`
3. **Headers:**
   - `Content-Type: application/json`
4. **Click:** Send
5. **Expected Status:** 200 OK

---

## 💻 Implementation dalam Aplikasi

### **Java (Retrofit):**

```java
// 1. Define interface
public interface ApiService {
    @GET("list")
    Call<RestaurantResponse> getRestaurantList();
}

// 2. Make API call
ApiService apiService = RetrofitClient.getApiService();
Call<RestaurantResponse> call = apiService.getRestaurantList();

call.enqueue(new Callback<RestaurantResponse>() {
    @Override
    public void onResponse(Call<RestaurantResponse> call, 
                          Response<RestaurantResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            RestaurantResponse data = response.body();
            List<Restaurant> restaurants = data.getRestaurants();
            // Use the data
        }
    }
    
    @Override
    public void onFailure(Call<RestaurantResponse> call, Throwable t) {
        // Handle error
    }
});
```

---

## 📊 Response Status Codes

| Status Code | Meaning | Description |
|-------------|---------|-------------|
| 200 | OK | Request berhasil |
| 400 | Bad Request | Request tidak valid |
| 404 | Not Found | Endpoint tidak ditemukan |
| 500 | Internal Server Error | Server error |

---

## ⚠️ Error Handling

### **Kemungkinan Error:**

1. **Network Error**
   - Tidak ada koneksi internet
   - Timeout
   - DNS resolution failed

2. **HTTP Error**
   - 404: Endpoint salah
   - 500: Server bermasalah

3. **Parsing Error**
   - JSON format tidak sesuai
   - Missing required fields

### **Best Practice:**

```java
call.enqueue(new Callback<RestaurantResponse>() {
    @Override
    public void onResponse(Call call, Response response) {
        if (response.isSuccessful() && response.body() != null) {
            RestaurantResponse data = response.body();
            
            // Check API error field
            if (!data.isError()) {
                // SUCCESS: Use the data
            } else {
                // API returned error
                Log.e(TAG, "API Error: " + data.getMessage());
            }
        } else {
            // HTTP error
            Log.e(TAG, "HTTP Error: " + response.code());
        }
    }
    
    @Override
    public void onFailure(Call call, Throwable t) {
        // Network error or exception
        Log.e(TAG, "Failure: " + t.getMessage());
    }
});
```

---

## 📌 Notes

- API ini **read-only** (hanya GET)
- Tidak perlu authentication
- Response selalu dalam format JSON
- Data bisa berubah sewaktu-waktu (sandbox environment)

---

## 🔗 Resources

- **API Base:** https://restaurant-api.dicoding.dev/
- **Documentation:** (Lihat website Dicoding)
- **Support:** Dicoding Academy

---

Dokumentasi ini berlaku untuk project **RestaurantApps**.
Last updated: December 2025

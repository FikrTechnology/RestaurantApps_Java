# Restaurant Apps - Android MVVM Project

**Status:**  Version 3.0 (MVVM + Clean Architecture)

Aplikasi Android untuk menampilkan list restaurant dari [Dicoding API](https://restaurant-api.dicoding.dev/) menggunakan **MVVM pattern** dan **Clean Architecture**.

---

##  Features

-  Display restaurant list dari API
-  Show restaurant details (name, city, rating, image)
-  GridView dengan Material Design CardView
-  Image loading dengan Glide
-  MVVM architecture untuk scalable code
-  Repository pattern untuk data abstraction
-  LiveData untuk reactive UI updates
-  Error handling dan loading state
-  Persist data saat screen rotation

---

##  Architecture

### Clean Architecture Layers

**PRESENTATION LAYER**
- Activity (MainActivity)
- ViewModel (RestaurantViewModel)
- Adapter (RestaurantAdapter)

**DATA LAYER**
- Remote API (RestaurantApiService, RetrofitClient)
- DTO (RestaurantDto, RestaurantResponse)
- Repository (RestaurantRepositoryImpl)

**DOMAIN LAYER**
- Entity (Restaurant)
- Repository Interface (RestaurantRepository)

---

##  Project Structure

\\\
app/src/main/java/com/example/restaurantapps/

 domain/                           Business logic (pure Java)
    model/
       Restaurant.java
    repository/
        RestaurantRepository.java

 data/                             Data sources (API, DB)
    remote/
       api/
          RestaurantApiService.java
          RetrofitClient.java
       response/
           RestaurantResponse.java
           RestaurantDto.java
    repository/
        RestaurantRepositoryImpl.java

 presentation/                     UI (Activity, ViewModel, Adapter)
     ui/
         activity/
            MainActivity.java
         adapter/
            RestaurantAdapter.java
         viewmodel/
             RestaurantViewModel.java
\\\

---

##  Documentation

- **QUICKSTART.md** - 5 minute overview
- **MVVM_GUIDE.md** - Deep dive into MVVM pattern

---

##  Quick Start

1. Open in Android Studio
2. File  Sync Now (wait for gradle sync)
3. Run on emulator/device (API 24+)
4. Restaurant list will load automatically

---

##  Tech Stack

- Java 11
- Android SDK 24+ (AndroidX)
- Retrofit 2.9.0
- Glide 4.15.1
- Lifecycle 2.6.2
- Coroutines 1.7.3

---

##  Learning Path

1. Read QUICKSTART.md (5 min)
2. Understand folder structure
3. Trace code from MainActivity  ViewModel  Repository  API
4. Read MVVM_GUIDE.md for deep understanding
5. Experiment with code changes

---

*Happy coding! Refer to MVVM_GUIDE.md and code comments for detailed explanations.*

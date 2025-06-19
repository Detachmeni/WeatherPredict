package com.example.asus.weatherapp.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.asus.weatherapp.db.Database;
import com.example.asus.weatherapp.db.WeatherDao;
import com.example.asus.weatherapp.repository.WeatherRepository;
import com.example.asus.weatherapp.http.WeatherWebService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {

    // --- DATABASE INJECTION ---

    @Provides
    @Singleton
    Database provideDatabase(Application application) {
        return Room.databaseBuilder(application,
                Database.class, "MyDatabase.db").fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    WeatherDao provideWeatherDao(Database database) { return database.weatherDao(); }

    // --- REPOSITORY INJECTION ---

    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Singleton
    WeatherRepository provideUserRepository(WeatherWebService webservice, WeatherDao userDao, Executor executor) {
        return new WeatherRepository(webservice, userDao, executor);
    }

    // --- NETWORK INJECTION ---

    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    @Provides
    Gson provideGson() { return new GsonBuilder().create(); }

    @Provides
    Retrofit provideRetrofit(Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    WeatherWebService provideApiWebservice(Retrofit restAdapter) {
        return restAdapter.create(WeatherWebService.class);
    }
}

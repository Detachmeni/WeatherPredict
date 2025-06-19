package com.example.asus.weatherapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.asus.weatherapp.domain.Weather;
import com.example.asus.weatherapp.repository.WeatherRepository;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class WeatherViewModel extends ViewModel {

    private LiveData<Weather> weather;
    private WeatherRepository weatherRepo;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public WeatherViewModel(WeatherRepository weatherRepo){
        this.weatherRepo = weatherRepo;
    }

    public void init(String city_name){
        Log.d("ViewModelInit:",city_name);
        disposables.add(weatherRepo.refreshWeather(city_name));
        weather = weatherRepo.getWeather(city_name);
    }

    public LiveData<Weather> getWeather() { return this.weather; }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}

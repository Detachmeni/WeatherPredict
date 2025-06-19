package com.example.asus.weatherapp.viewmodel;

import com.example.asus.weatherapp.base.WeatherFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract WeatherFragment contributeWeatherFragment();

}

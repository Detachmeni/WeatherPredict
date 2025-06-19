package com.example.asus.weatherapp.module;

import com.example.asus.weatherapp.viewmodel.FragmentModule;
import com.example.asus.weatherapp.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector(modules = FragmentModule.class)
    abstract MainActivity contributeMainActivity();
}

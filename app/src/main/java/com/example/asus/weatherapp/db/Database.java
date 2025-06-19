package com.example.asus.weatherapp.db;

import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.asus.weatherapp.domain.Weather;
import com.example.asus.weatherapp.converter.DateConverter;

@android.arch.persistence.room.Database(entities = {Weather.class}, version = 10, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class Database extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile Database INSTANCE;

    // --- DAO ---
    public abstract WeatherDao weatherDao();

}
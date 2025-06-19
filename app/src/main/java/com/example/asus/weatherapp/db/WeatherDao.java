package com.example.asus.weatherapp.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.asus.weatherapp.domain.Weather;

import java.util.Date;

import io.reactivex.Maybe;


@Dao
public interface WeatherDao {

    @Insert()
    void save(Weather weather);

    @Query("SELECT * FROM weather WHERE city_name like :name")
    LiveData<Weather> load(String name);

    @Query("Delete from weather where weather_id= :weather_id")
    void delete(int weather_id);

    // 添加 RxJava 支持的方法
    @Query("SELECT * FROM weather WHERE city_name = :city_name AND lastRefresh > :lastRefreshMax LIMIT 1")
    Maybe<Weather> hasWeather(String city_name, Date lastRefreshMax);

}

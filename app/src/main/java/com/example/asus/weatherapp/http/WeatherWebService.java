package com.example.asus.weatherapp.http;

import com.example.asus.weatherapp.domain.Weather;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherWebService {

//    @GET("forecast")
//    Call<Weather> getWeather(@Query("q") String city_name,@Query("appid") String api_key);

    // RxJava 形式
    @GET("weather")
    Single<Weather> getWeatherRx(@Query("q") String city, @Query("appid") String apiKey);
}

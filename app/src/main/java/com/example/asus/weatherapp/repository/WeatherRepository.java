package com.example.asus.weatherapp.repository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import android.arch.lifecycle.LiveData;
import android.util.Log;
import android.widget.Toast;

import com.example.asus.weatherapp.domain.Weather;
import com.example.asus.weatherapp.app.App;
import com.example.asus.weatherapp.db.WeatherDao;
import com.example.asus.weatherapp.http.WeatherWebService;

import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class WeatherRepository {

    private static int FRESH_TIMEOUT_IN_MINUTES = 3;

    private final WeatherWebService webService;
    private final WeatherDao weatherDao;
    private final Executor executor;

    @Inject
    public WeatherRepository(WeatherWebService webService, WeatherDao weatherDao, Executor executor) {
        this.webService = webService;
        this.weatherDao = weatherDao;
        this.executor = executor;
    }

    public LiveData<Weather> getWeather(String city_name) {
        Log.d("RepoGetWeather:",city_name);
        refreshWeather(city_name);
        return weatherDao.load(city_name);
    }

    public Disposable refreshWeather(final String cityName) {
        // 使用 RxJava 检查缓存并执行网络请求
        return checkCacheAndFetchData(cityName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        weather -> {
                            Toast.makeText(App.context, "Data refreshed from network!", Toast.LENGTH_LONG).show();
                        },
                        error -> {
                            Log.e("WeatherRefresh", "Error in refreshWeather", error); // 使用带异常参数的Log方法
                        }
                );
    }

    /**
     * RxJava 核心方法：检查缓存并获取网络数据
     */
    private Single<Weather> checkCacheAndFetchData(String cityName) {
        return weatherDao.hasWeather(cityName, getMaxRefreshTime(new Date()))
                // 将 Maybe<Weather> 转换为 Single<Weather>
                .toSingle()
                // 处理缓存有效情况
                .flatMap(cachedWeather -> Single.just(cachedWeather))
                // 处理缓存无效情况
                .onErrorResumeNext(error -> {
                    if (error instanceof NoSuchElementException) {
                        // 缓存为空，执行网络请求
                        return fetchFromNetwork(cityName);
                    }
                    // 其他错误直接传递
                    return Single.error(error);
                });
    }

    /**
     * 从网络获取数据并保存到数据库
     */
    private Single<Weather> fetchFromNetwork(String cityName) {
        return webService.getWeatherRx(cityName, "f20dae443685124d02f23f31769e14bf")
                .flatMap(networkWeather -> {
                    if (networkWeather == null) {
                        // 处理网络返回 null 的情况
                        return Single.error(new NullPointerException("Network response is null"));
                    }
                    networkWeather.setLastRefresh(new Date());
                    // 保存到数据库并返回数据
                    return saveWeather(networkWeather);
                })
                .onErrorResumeNext(throwable ->
                        Single.error(new Exception("Failed to fetch from network", throwable))
                );
    }

    /**
     * 保存天气数据到数据库
     */
    private Single<Weather> saveWeather(Weather weather) {
        return Completable.fromAction(() -> {
                    if (weather != null) {
                        weatherDao.save(weather);
                    } else {
                        throw new NullPointerException("Attempted to save null weather");
                    }
                })
                .andThen(Single.just(weather))
                .onErrorResumeNext(throwable ->
                        Single.error(new Exception("Failed to save weather", throwable))
                )
                .subscribeOn(Schedulers.io());
    }

    private Date getMaxRefreshTime(Date currentDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MINUTE, -FRESH_TIMEOUT_IN_MINUTES);
        return cal.getTime();
    }
}
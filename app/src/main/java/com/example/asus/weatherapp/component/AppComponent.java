package com.example.asus.weatherapp.component;

import android.app.Application;

import com.example.asus.weatherapp.viewmodel.FragmentModule;
import com.example.asus.weatherapp.app.App;
import com.example.asus.weatherapp.module.ActivityModule;
import com.example.asus.weatherapp.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules={AndroidSupportInjectionModule.class, ActivityModule.class, FragmentModule.class, AppModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(App app);
}

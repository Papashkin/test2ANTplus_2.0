package com.example.test2antplus

import android.app.Application
import com.example.test2antplus.di.AppComponent
import com.example.test2antplus.di.DaggerAppComponent
import com.example.test2antplus.di.modules.AppModule
import com.example.test2antplus.di.modules.NavigationModule

class MainApplication: Application() {
    companion object {
        lateinit var graph: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        graph = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .navigationModule(NavigationModule())
            .build()

        graph.inject(this)
    }
}
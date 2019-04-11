package com.example.test2antplus

import android.app.Application
import com.example.test2antplus.di.AppComponent
import com.example.test2antplus.di.DaggerAppComponent
import com.example.test2antplus.di.modules.AppModule
import com.example.test2antplus.di.modules.NavigationModule

class MainApplication: Application() {
    companion object {
        @JvmStatic
        lateinit var graph: AppComponent

        const val ACTION_WORK_SENDING = "com.example.test2antplus.presentation.view.workScreen.WorkFragment"
        const val ACTION_PROGRAM_SETTINGS = "com.example.test2antplus.presentation.view.programs.ProgramsFragment"
        const val UPD_PROGRAMS_LIST = "programs list"
        const val ARGS_PROGRAM = "selected program"

        const val PERMISSION_FOR_APP = 1

        var PROGRAM_IMAGES_PATH: String? = null
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
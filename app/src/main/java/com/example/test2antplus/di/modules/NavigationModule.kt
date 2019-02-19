package com.example.test2antplus.di.modules

import com.example.test2antplus.navigation.AppRouter
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import javax.inject.Singleton

@Module
class NavigationModule {

    @Provides
    @Singleton
    fun getCicerone() = Cicerone.create(AppRouter())

    @Provides
    @Singleton
    fun getRouter(cicerone: Cicerone<AppRouter>) = cicerone.router
}
package com.antsfamily.biketrainer.di.modules

import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Module
class NavigationModule {

    @Provides
    @Singleton
    fun getCicerone() = Cicerone.create()

    @Provides
    @Singleton
    fun getRouter(cicerone: Cicerone<Router>) = cicerone.router

    @Provides
    @Singleton
    fun getNavigatorHolder(cicerone: Cicerone<Router>) = cicerone.navigatorHolder
}
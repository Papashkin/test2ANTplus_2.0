package com.example.test2antplus.di.modules

import android.arch.persistence.room.Room
import android.content.Context
import android.support.annotation.NonNull
import com.example.test2antplus.data.ProfileDao
import com.example.test2antplus.data.ProfilesDatabase
import com.example.test2antplus.data.ProfilesRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(@NonNull private val context: Context) {

    @Provides
    @Singleton
    fun getContext() = context

    @Provides
    @Singleton
    fun getDatabase(): ProfilesDatabase = Room.databaseBuilder(
        context,
        ProfilesDatabase::class.java,
        "profiles"
    ).build()

    @Provides
    @Singleton
    fun getProfileDao(database: ProfilesDatabase) = database.profileDao()


    @Provides
    @Singleton
    fun getProfilesRepository(dao: ProfileDao) = ProfilesRepository(dao)
}
package com.example.test2antplus.di.modules

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Room
import com.example.test2antplus.data.profiles.ProfileDao
import com.example.test2antplus.data.profiles.ProfilesDatabase
import com.example.test2antplus.data.profiles.ProfilesRepository
import com.example.test2antplus.data.programs.ProgramDao
import com.example.test2antplus.data.programs.ProgramsDatabase
import com.example.test2antplus.data.programs.ProgramsRepository
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
    fun getProfilesDatabase(): ProfilesDatabase = Room.databaseBuilder(
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


    @Provides
    @Singleton
    fun getProgramsDatabase(): ProgramsDatabase = Room.databaseBuilder(
        context,
        ProgramsDatabase::class.java,
        "programs"
    ).build()

    @Provides
    @Singleton
    fun getProgramDao(database: ProgramsDatabase) = database.programDao()


    @Provides
    @Singleton
    fun getProgramsRepository(dao: ProgramDao) = ProgramsRepository(dao)
}
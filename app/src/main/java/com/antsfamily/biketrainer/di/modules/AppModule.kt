package com.antsfamily.biketrainer.di.modules

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Room
import com.antsfamily.biketrainer.data.db.profiles.ProfilesDao
import com.antsfamily.biketrainer.data.db.profiles.ProfilesDatabase
import com.antsfamily.biketrainer.data.repositories.profiles.ProfilesRepository
import com.antsfamily.biketrainer.data.db.programs.ProgramsDao
import com.antsfamily.biketrainer.data.db.programs.ProgramsDatabase
import com.antsfamily.biketrainer.data.repositories.programs.ProgramsRepository
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
    fun getProfilesRepository(dao: ProfilesDao) = ProfilesRepository(dao)


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
    fun getProgramsRepository(dao: ProgramsDao) = ProgramsRepository(dao)
}
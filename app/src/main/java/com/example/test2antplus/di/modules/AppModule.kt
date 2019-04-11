package com.example.test2antplus.di.modules

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Room
import com.example.test2antplus.data.db.profiles.ProfilesDao
import com.example.test2antplus.data.db.profiles.ProfilesDatabase
import com.example.test2antplus.data.repositories.profiles.ProfilesRepository
import com.example.test2antplus.data.db.programs.ProgramsDao
import com.example.test2antplus.data.db.programs.ProgramsDatabase
import com.example.test2antplus.data.repositories.programs.ProgramsRepository
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
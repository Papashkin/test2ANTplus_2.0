package com.antsfamily.biketrainer.di.modules

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Room
import com.antsfamily.biketrainer.data.local.database.AntsBikeTrainerDatabase
import com.antsfamily.biketrainer.data.local.repositories.ProfilesDao
import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.data.local.repositories.ProgramsDao
import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
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
    fun getBikeTrainerDatabase(): AntsBikeTrainerDatabase = Room.databaseBuilder(
        context,
        AntsBikeTrainerDatabase::class.java,
        "AntBikeTrainer"
    ).build()

    @Provides
    @Singleton
    fun getProfileDao(database: AntsBikeTrainerDatabase) = database.profileDao()

    @Provides
    @Singleton
    fun getProfilesRepository(dao: ProfilesDao) = ProfilesRepository(dao)

    @Provides
    @Singleton
    fun getProgramDao(database: AntsBikeTrainerDatabase) = database.programsDao()

    @Provides
    @Singleton
    fun getProgramsRepository(dao: ProgramsDao) = ProgramsRepository(dao)
}

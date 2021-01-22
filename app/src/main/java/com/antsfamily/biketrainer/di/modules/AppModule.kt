package com.antsfamily.biketrainer.di.modules

import android.content.Context
import androidx.room.Room
import com.antsfamily.biketrainer.data.local.database.AntsBikeTrainerDatabase
import com.antsfamily.biketrainer.data.local.repositories.ProfilesDao
import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.data.local.repositories.ProgramsDao
import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

//    @Provides
//    fun provideContext(context: Context) = context

    @Provides
    @Singleton
    fun provideBikeTrainerDatabase(@ApplicationContext appContext: Context): AntsBikeTrainerDatabase =
        Room.databaseBuilder(appContext, AntsBikeTrainerDatabase::class.java, "AntBikeTrainer")
            .build()

    @Provides
    fun provideProfileDao(database: AntsBikeTrainerDatabase) = database.profileDao()

    @Provides
    fun provideProfilesRepository(dao: ProfilesDao) = ProfilesRepository(dao)

    @Provides
    fun provideProgramDao(database: AntsBikeTrainerDatabase) = database.programsDao()

    @Provides
    fun provideProgramsRepository(dao: ProgramsDao) = ProgramsRepository(dao)
}

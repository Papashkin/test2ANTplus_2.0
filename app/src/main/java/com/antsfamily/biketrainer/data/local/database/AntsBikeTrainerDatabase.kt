package com.antsfamily.biketrainer.data.local.database

import androidx.room.*
import com.antsfamily.biketrainer.data.models.Profile
import com.antsfamily.biketrainer.data.local.repositories.ProfilesDao
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.data.local.repositories.ProgramsDao
import com.antsfamily.biketrainer.data.models.program.ProgramDataConverter

@Database(entities = [Profile::class, Program::class], version = 1, exportSchema = false)
@TypeConverters(ProgramDataConverter::class)
abstract class AntsBikeTrainerDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfilesDao
    abstract fun programsDao(): ProgramsDao
}





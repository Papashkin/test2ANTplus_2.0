package com.antsfamily.biketrainer.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.antsfamily.biketrainer.data.models.profile.Profile
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.data.models.program.ProgramDataConverter

@Database(entities = [Profile::class, Program::class], version = 1, exportSchema = false)
@TypeConverters(ProgramDataConverter::class)
abstract class AntsBikeTrainerDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun programsDao(): ProgramDao
}





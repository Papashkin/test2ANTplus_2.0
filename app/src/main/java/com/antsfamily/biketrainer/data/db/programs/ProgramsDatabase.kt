package com.antsfamily.biketrainer.data.db.programs

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antsfamily.biketrainer.data.repositories.programs.Program

@Database(entities = [Program::class], version = 1)
abstract class ProgramsDatabase: RoomDatabase() {
    abstract fun programDao(): ProgramsDao
}
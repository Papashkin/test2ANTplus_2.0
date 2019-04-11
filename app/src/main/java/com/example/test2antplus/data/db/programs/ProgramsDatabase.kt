package com.example.test2antplus.data.db.programs

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.test2antplus.data.repositories.programs.Program

@Database(entities = [Program::class], version = 1)
abstract class ProgramsDatabase: RoomDatabase() {
    abstract fun programDao(): ProgramsDao
}
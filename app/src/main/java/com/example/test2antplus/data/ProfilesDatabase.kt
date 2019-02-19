package com.example.test2antplus.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.test2antplus.Profile

@Database (entities = [Profile::class], version = 1)
abstract class ProfilesDatabase: RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}
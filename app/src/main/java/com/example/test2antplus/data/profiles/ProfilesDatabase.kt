package com.example.test2antplus.data.profiles

import androidx.room.Database
import androidx.room.RoomDatabase

@Database (entities = [Profile::class], version = 1)
abstract class ProfilesDatabase: RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}
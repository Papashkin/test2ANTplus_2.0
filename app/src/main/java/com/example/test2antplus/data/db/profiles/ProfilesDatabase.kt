package com.example.test2antplus.data.db.profiles

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.test2antplus.data.repositories.profiles.Profile

@Database (entities = [Profile::class], version = 1)
abstract class ProfilesDatabase: RoomDatabase() {
    abstract fun profileDao(): ProfilesDao
}
package com.antsfamily.biketrainer.data.db.profiles

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antsfamily.biketrainer.data.repositories.profiles.Profile

@Database (entities = [Profile::class], version = 1)
abstract class ProfilesDatabase: RoomDatabase() {
    abstract fun profileDao(): ProfilesDao
}
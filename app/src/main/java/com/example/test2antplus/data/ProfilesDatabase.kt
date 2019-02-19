package com.example.test2antplus.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.example.test2antplus.Profile

@Database (entities = [Profile::class], version = 1)
abstract class ProfilesDatabase: RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}
package com.antsfamily.biketrainer.data.db.profiles

import androidx.room.*
import com.antsfamily.biketrainer.data.repositories.profiles.Profile

@Dao
interface ProfilesDao {

    @Query("SELECT * from profile")
    suspend fun getAll(): List<Profile>

    @Query("Select * from profile where name = :profileName")
    suspend fun getProfile(profileName: String): Profile?

    @Insert
    suspend fun addProfile(profile: Profile)

    @Update
    suspend fun updateProfile(profile: Profile)

    @Delete
    suspend fun deleteProfile(profile: Profile)
}
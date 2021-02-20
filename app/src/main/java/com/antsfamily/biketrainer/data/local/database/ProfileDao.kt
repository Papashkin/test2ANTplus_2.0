package com.antsfamily.biketrainer.data.local.database

import androidx.room.*
import com.antsfamily.biketrainer.data.models.profile.Profile
import com.antsfamily.biketrainer.data.models.profile.ProfileWithPrograms

@Dao
abstract class ProfileDao {

    @Query("SELECT * from profile")
    abstract suspend fun getAll(): List<Profile>

    @Query("Select * from profile where name = :profileName")
    abstract suspend fun getProfile(profileName: String): Profile?

    @Query("Select * from profile where isSelected = 1")
    abstract suspend fun getSelectedProfile(): Profile?

    @Query("Update profile set isSelected = 0")
    abstract suspend fun clearSelectedProfiles()

    @Transaction
    @Query("SELECT * from profile where isSelected = 1")
    abstract suspend fun getSelectedProfileWithPrograms(): ProfileWithPrograms

    @Insert
    abstract suspend fun addProfile(profile: Profile)

    @Update
    abstract suspend fun updateProfile(profile: Profile)

    @Delete
    abstract suspend fun deleteProfile(profile: Profile)
}

package com.antsfamily.biketrainer.data.local.repositories

import androidx.room.*
import com.antsfamily.biketrainer.data.models.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Dao
interface ProfilesDao {

    @Query("SELECT * from profile")
    suspend fun getAll(): List<Profile>

    @Query("Select * from profile where name = :profileName")
    suspend fun getProfile(profileName: String): Profile?

    @Query("Select * from profile where is_selected = 1")
    suspend fun getSelectedProfile(): Profile?

    @Query("Update profile set is_selected = 0")
    suspend fun clearSelectedProfiles()

    @Insert
    suspend fun addProfile(profile: Profile)

    @Update
    suspend fun updateProfile(profile: Profile)

    @Delete
    suspend fun deleteProfile(profile: Profile)
}

@Singleton
class ProfilesRepository @Inject constructor(private val profilesDao: ProfilesDao) {
    suspend fun getAllProfiles(): List<Profile> = profilesDao.getAll()
    suspend fun getSelectedProfile(): Profile? = profilesDao.getSelectedProfile()
    suspend fun clearSelectedProfile() = profilesDao.clearSelectedProfiles()
    suspend fun getProfileByName(name: String): Profile? = profilesDao.getProfile(name)
    suspend fun insertProfile(profile: Profile) = profilesDao.addProfile(profile)
    suspend fun updateProfile(profile: Profile) = profilesDao.updateProfile(profile)
    suspend fun removeProfile(profile: Profile) = profilesDao.deleteProfile(profile)
}

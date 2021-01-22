package com.antsfamily.biketrainer.data.local.repositories

import androidx.lifecycle.LiveData
import androidx.room.*
import com.antsfamily.biketrainer.data.models.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Dao
interface ProfilesDao {

    @Query("SELECT * from profile")
    fun getAll(): LiveData<List<Profile>>

    @Query("Select * from profile where name = :profileName")
    suspend fun getProfile(profileName: String): Profile?

    @Insert
    suspend fun addProfile(profile: Profile)

    @Update
    suspend fun updateProfile(profile: Profile)

    @Delete
    suspend fun deleteProfile(profile: Profile)
}

@Singleton
class ProfilesRepository @Inject constructor(private val profilesDao: ProfilesDao)  {

    fun getAllProfiles(): LiveData<List<Profile>> = profilesDao.getAll()

    suspend fun getProfileByName(name: String): Profile? = profilesDao.getProfile(name)

    suspend fun insertProfile(profile: Profile) = profilesDao.addProfile(profile)

    suspend fun updateProfile(profile: Profile) = profilesDao.updateProfile(profile)

    suspend fun removeProfile(profile: Profile) = profilesDao.deleteProfile(profile)
}

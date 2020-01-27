package com.example.test2antplus.data.repositories.profiles

import com.example.test2antplus.data.db.profiles.ProfilesDao
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ProfilesRepository @Inject constructor(private val profilesDao: ProfilesDao) {

    suspend fun getAllProfiles(): List<Profile> = profilesDao.getAll()

    suspend fun getProfileByName(name: String): Profile? = profilesDao.getProfile(name)

    suspend fun insertProfile(profile: Profile) = profilesDao.addProfile(profile)

    suspend fun updateProfile(profile: Profile) = profilesDao.updateProfile(profile)

    suspend fun removeProfile(profile: Profile) = profilesDao.deleteProfile(profile)
}
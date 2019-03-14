package com.example.test2antplus.data.profiles

import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilesRepository @Inject constructor(private val profileDao: ProfileDao) {

    fun getAllProfiles(): LiveData<List<Profile>> = profileDao.getAll()

//    fun getProfileByName(name: String): Single<Profile> = profileDao.getProfile(name)

    fun insertProfile(profile: Profile) {
        profileDao.addProfile(profile)
    }

    fun removeProfile(profile: Profile) {
        profileDao.deleteProfile(profile)
    }
}
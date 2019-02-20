package com.example.test2antplus.data

import androidx.lifecycle.LiveData
import com.example.test2antplus.Profile
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
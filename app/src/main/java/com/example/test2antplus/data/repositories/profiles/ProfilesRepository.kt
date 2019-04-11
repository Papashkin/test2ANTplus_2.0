package com.example.test2antplus.data.repositories.profiles

import com.example.test2antplus.data.db.profiles.ProfilesDao
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilesRepository @Inject constructor(private val profilesDao: ProfilesDao) {

    fun getAllProfiles(): Flowable<List<Profile>> = profilesDao.getAll()

    fun getProfileByName(name: String): Profile = profilesDao.getProfile(name)

    fun insertProfile(profile: Profile) {
        profilesDao.addProfile(profile)
    }

    fun updateProfile(profile: Profile) {
        profilesDao.updateProfile(profile)
    }

    fun removeProfile(profile: Profile) {
        profilesDao.deleteProfile(profile)
    }
}
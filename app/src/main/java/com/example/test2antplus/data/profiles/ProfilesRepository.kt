package com.example.test2antplus.data.profiles

import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilesRepository @Inject constructor(private val profileDao: ProfileDao) {

    fun getAllProfiles(): Flowable<List<Profile>> = profileDao.getAll()

    fun getProfileByName(name: String): Single<Profile> = profileDao.getProfile(name)

    fun insertProfile(profile: Profile) {
        profileDao.addProfile(profile)
    }

    fun updateProfile(profile: Profile) {
        profileDao.updateProfile(profile)
    }

    fun removeProfile(profile: Profile) {
        profileDao.deleteProfile(profile)
    }
}
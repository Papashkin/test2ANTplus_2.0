package com.antsfamily.biketrainer.data.local.repositories

import com.antsfamily.biketrainer.data.local.database.ProfileDao
import com.antsfamily.biketrainer.data.models.profile.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilesRepository @Inject constructor(private val dao: ProfileDao) {
    suspend fun getAllProfiles(): List<Profile> = dao.getAll()
    suspend fun getSelectedProfileWithPrograms() = dao.getSelectedProfileWithPrograms()
    suspend fun getSelectedProfile(): Profile? = dao.getSelectedProfile()
    suspend fun clearSelectedProfile() = dao.clearSelectedProfiles()
    suspend fun getProfileByName(name: String): Profile? = dao.getProfile(name)
    suspend fun insertProfile(profile: Profile) = dao.addProfile(profile)
    suspend fun updateProfile(profile: Profile) = dao.updateProfile(profile)
    suspend fun removeProfile(profile: Profile) = dao.deleteProfile(profile)
}

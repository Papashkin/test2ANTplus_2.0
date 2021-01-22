package com.antsfamily.biketrainer.presentation.profiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.antsfamily.biketrainer.R
import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.data.models.Profile
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfilesViewModel @Inject constructor(
    private val profilesRepository: ProfilesRepository
) : StatefulViewModel<ProfilesViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = false,
        val profiles: List<Profile> = emptyList(),
        val profileSettings: Profile? = null,
        val exitDialog: Boolean = false,
        val deletingSnackBar: String? = null
    )

    private var existedProfile: Profile? = null
    private var profileToDelete: Profile? = null
    private var selectedProfile: Profile? = null
    private var newProfile: Profile = Profile.empty()
    private var deletePosition = -1
    private var isNewProfile = true

    val profiles: LiveData<List<Profile>> = liveData {
        emitSource(profilesRepository.getAllProfiles())
    }
    val profileSettings: MutableLiveData<Profile?> = MutableLiveData(null)
    val exitDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    val deletingSnackBar: MutableLiveData<String?> = MutableLiveData(null)

    fun selectProfile(id: Int, isWorkingTime: Boolean) {
        if (isWorkingTime) {
            selectedProfile = profiles.value?.first { it.getId() == id }
//            router.navigateTo(
//                FragmentScreens.ProgramScreen(
//                    isWorkingTime,
//                    selectedProfile!!.getName()
//                )
//            )
        }
    }

    fun clearValues() {
        clearLiveDataValues()
        profileSettings.postValue(null)
        exitDialog.postValue(false)
        deletingSnackBar.postValue(null)
    }

    fun onDeleteClick(pos: Int) = launch {
        try {
            deletePosition = pos
            profileToDelete = profiles.value?.get(pos)
            profilesRepository.removeProfile(profileToDelete!!)
            deletingSnackBar.postValue(profileToDelete?.getName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun undoDelete() = launch {
        try {
            profilesRepository.insertProfile(profileToDelete!!)
        } catch (e: Exception) {
            showToast(R.string.new_profile_failed_to_create)
            e.printStackTrace()
        }
    }

    fun onEditProfileClick(id: Int) {
        isNewProfile = false
        existedProfile = profiles.value?.first { it.getId() == id }
        profileSettings.postValue(existedProfile)
    }

    fun addNewProfileClick() {
        isNewProfile = true
        profileSettings.postValue(newProfile)
    }

    private fun checkTheProfiles() = launch {
        try {
            val profileWithSameName = profilesRepository.getProfileByName(newProfile.getName())
            if (profileWithSameName != null) {
                showToast(R.string.new_profile_existed)
            } else {
                createProfile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createProfile() = launch {
        try {
            if (newProfile.isFilled()) {
                profilesRepository.insertProfile(newProfile)
                clearNewProfile()
                hideKeyboard()
            } else {
                showToast(R.string.invalid_data)
            }
        } catch (e: Exception) {
            showToast(R.string.new_profile_failed_to_create)
            e.printStackTrace()
        }
    }

    private fun updateProfile() = launch {
        try {
            if (existedProfile!!.isFilled()) {
                profilesRepository.updateProfile(existedProfile!!)
                profileSettings.postValue(null)
            } else {
                showToast(R.string.invalid_data)
            }
        } catch (e: Exception) {
            showToast(R.string.new_profile_failed_to_update)
            e.printStackTrace()
        }
    }

    fun setName(name: String) {
        if (name.isBlank()) return
        if (isNewProfile) {
            newProfile.setName(name)
        } else {
            existedProfile?.setName(name)
        }
    }

    fun setAge(age: Int) {
        if (age == 0) return
        if (isNewProfile) {
            newProfile.setAge(age)
        } else {
            existedProfile?.setAge(age)
        }
    }

    fun setGender(gender: String) {
        if (isNewProfile) {
            newProfile.setGender(gender)
        } else {
            existedProfile?.setGender(gender)
        }
    }

    fun setWeight(weight: Float) {
        if (weight == 0.0F) return
        if (isNewProfile) {
            newProfile.setWeight(weight)
        } else {
            existedProfile?.setWeight(weight)
        }
    }

    fun setHeight(height: Float) {
        if (height == 0.0F) return
        if (isNewProfile) {
            newProfile.setHeight(height)
        } else {
            existedProfile?.setHeight(height)
        }
    }

    fun onCreateClick() {
        if (isNewProfile) {
            checkTheProfiles()
        } else {
            updateProfile()
        }
    }

    fun clearNewProfile() {
        profileSettings.postValue(null)
        newProfile = Profile.empty()
    }
}

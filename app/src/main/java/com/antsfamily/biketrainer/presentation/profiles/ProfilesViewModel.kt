package com.antsfamily.biketrainer.presentation.profiles

import androidx.lifecycle.MutableLiveData
import com.antsfamily.biketrainer.data.models.Profile
import com.antsfamily.biketrainer.domain.usecase.GetProfileUseCase
import com.antsfamily.biketrainer.navigation.ProfileToCreateProfile
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfilesViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
) : StatefulViewModel<ProfilesViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = true,
        val profiles: List<Profile> = emptyList(),
        val isProfilesVisible: Boolean = false,
        val isEmptyProfileVisible: Boolean = false
    )

//    private var existedProfile: Profile? = null
//    private var profileToDelete: Profile? = null
//    private var selectedProfile: Profile? = null
//    private var newProfile: Profile = Profile.empty()
//    private var deletePosition = -1
//    private var isNewProfile = true

    //    val profiles: LiveData<List<Profile>> = liveData {
//        emitSource(profilesRepository.getAllProfiles())
//    }
    val profileSettings: MutableLiveData<Profile?> = MutableLiveData(null)
    val exitDialog: MutableLiveData<Boolean> = MutableLiveData(false)
    val deletingSnackBar: MutableLiveData<String?> = MutableLiveData(null)

    init {
        getProfiles()
    }

//    fun selectProfile(id: Int, isWorkingTime: Boolean) {
//        if (isWorkingTime) {
//            selectedProfile = profiles.value?.first { it.getId() == id }
//            router.navigateTo(
//                FragmentScreens.ProgramScreen(
//                    isWorkingTime,
//                    selectedProfile!!.getName()
//                )
//            )
//        }
//    }

//    fun clearValues() {
//        clearLiveDataValues()
//        profileSettings.postValue(null)
//        exitDialog.postValue(false)
//        deletingSnackBar.postValue(null)
//    }

//    fun onDeleteClick(pos: Int) = launch {
//        try {
//            deletePosition = pos
//            profileToDelete = profiles.value?.get(pos)
//            profilesRepository.removeProfile(profileToDelete!!)
//            deletingSnackBar.postValue(profileToDelete?.getName())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

//    fun undoDelete() = launch {
//        try {
//            profilesRepository.insertProfile(profileToDelete!!)
//        } catch (e: Exception) {
//            showToast(R.string.new_profile_failed_to_create)
//            e.printStackTrace()
//        }
//    }

//    fun onEditProfileClick(id: Int) {
//        isNewProfile = false
//        existedProfile = profiles.value?.first { it.getId() == id }
//        profileSettings.postValue(existedProfile)
//    }

    fun onBackButtonClick() {
        navigateBack()
    }

    fun addNewProfileClick() {
        navigateTo(ProfileToCreateProfile)
    }

    private fun getProfiles() = launch {
        getProfileUseCase.run(Unit)
            .handleResult(::handleProfileSuccessResult, ::handleProfileFailureResult)
//        getProfileUseCase.run(Unit).handleResult({
//            changeState { state ->
//                state.copy(
//                    profiles = it,
//                    isLoading = false,
//                    isProfilesVisible = it.isNotEmpty(),
//                    isEmptyProfileVisible = it.isEmpty()
//                )
//            }
//        }, {
//            changeState {
//                it.copy(
//                    profiles = emptyList(),
//                    isEmptyProfileVisible = false,
//                    isLoading = false,
//                    isProfilesVisible = false
//                )
//            }
//        })
    }

    private fun handleProfileSuccessResult(profiles: List<Profile>) {
        changeState {
            it.copy(
                profiles = profiles,
                isLoading = false,
                isProfilesVisible = profiles.isNotEmpty(),
                isEmptyProfileVisible = profiles.isEmpty()
            )
        }
    }

    private fun handleProfileFailureResult(error: Error) {
        changeState {
            it.copy(
                profiles = emptyList(),
                isEmptyProfileVisible = false,
                isLoading = false,
                isProfilesVisible = false
            )
        }
    }

//    private fun createProfile() = launch {
//        try {
//            if (newProfile.isFilled()) {
////                profilesRepository.insertProfile(newProfile)
//                clearNewProfile()
//                hideKeyboard()
//            } else {
//                showToast(R.string.invalid_data)
//            }
//        } catch (e: Exception) {
//            showToast(R.string.new_profile_failed_to_create)
//            e.printStackTrace()
//        }
//    }

//    private fun updateProfile() = launch {
//        try {
//            if (existedProfile!!.isFilled()) {
////                profilesRepository.updateProfile(existedProfile!!)
//                profileSettings.postValue(null)
//            } else {
//                showToast(R.string.invalid_data)
//            }
//        } catch (e: Exception) {
//            showToast(R.string.new_profile_failed_to_update)
//            e.printStackTrace()
//        }
//    }

//    fun setName(name: String) {
//        if (name.isBlank()) return
//        if (isNewProfile) {
//            newProfile.setName(name)
//        } else {
//            existedProfile?.setName(name)
//        }
//    }

//    fun setAge(age: Int) {
//        if (age == 0) return
//        if (isNewProfile) {
//            newProfile.setAge(age)
//        } else {
//            existedProfile?.setAge(age)
//        }
//    }

//    fun setGender(gender: String) {
//        if (isNewProfile) {
//            newProfile.setGender(gender)
//        } else {
//            existedProfile?.setGender(gender)
//        }
//    }

//    fun setWeight(weight: Float) {
//        if (weight == 0.0F) return
//        if (isNewProfile) {
//            newProfile.setWeight(weight)
//        } else {
//            existedProfile?.setWeight(weight)
//        }
//    }

//    fun setHeight(height: Float) {
//        if (height == 0.0F) return
//        if (isNewProfile) {
//            newProfile.setHeight(height)
//        } else {
//            existedProfile?.setHeight(height)
//        }
//    }

//    fun onCreateClick() {
//        if (isNewProfile) {
//            checkTheProfiles()
//        } else {
//            updateProfile()
//        }
//    }

//    fun clearNewProfile() {
//        profileSettings.postValue(null)
//        newProfile = Profile.empty()
//    }
}

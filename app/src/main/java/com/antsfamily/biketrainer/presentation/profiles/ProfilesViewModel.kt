package com.antsfamily.biketrainer.presentation.profiles

import com.antsfamily.biketrainer.data.models.profile.Profile
import com.antsfamily.biketrainer.domain.usecase.GetProfileUseCase
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

    init {
        getProfiles()
    }

    fun onBackButtonClick() {
        navigateBack()
    }

    fun addNewProfileClick() {
        // TODO: 14.02.2021
    }

    private fun getProfiles() = launch {
        getProfileUseCase.run(Unit)
            .handleResult(::handleProfileSuccessResult, ::handleProfileFailureResult)
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
}

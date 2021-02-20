package com.antsfamily.biketrainer.presentation.splash

import android.os.Handler
import android.os.Looper
import com.antsfamily.biketrainer.data.models.profile.Profile
import com.antsfamily.biketrainer.domain.Result
import com.antsfamily.biketrainer.domain.usecase.GetSelectedProfileUseCase
import com.antsfamily.biketrainer.navigation.SplashToCreateProfile
import com.antsfamily.biketrainer.navigation.SplashToHome
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import java.lang.Error
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val getSelectedProfileUseCase: GetSelectedProfileUseCase
) : StatefulViewModel<SplashViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = false
    )

    fun onResume() {
        showLoading()
        Handler(Looper.getMainLooper()).postDelayed(::getSelectedProfile, DELAY)
    }

    private fun getSelectedProfile() {
        getSelectedProfileUseCase(Unit, ::handleSelectedProfileResult)
    }

    private fun handleSelectedProfileResult(result: Result<Profile?, Error>) {
        when (result) {
            is Result.Success -> handleSuccessResult(result.successData)
            else -> navigateToCreateProfile()
        }
    }

    private fun handleSuccessResult(data: Profile?) {
        data?.let { navigateToStart() } ?: navigateToCreateProfile()
    }

    private fun showLoading() {
        changeState { it.copy(isLoading = true) }
    }

    private fun navigateToStart() {
        navigateTo(SplashToHome)
        hideLoading()
    }

    private fun navigateToCreateProfile() {
        navigateTo(SplashToCreateProfile)
        hideLoading()
    }

    private fun hideLoading() {
        changeState { it.copy(isLoading = false) }
    }

    companion object {
        private const val DELAY = 1000L
    }
}

package com.antsfamily.biketrainer.presentation.splash

import android.os.Handler
import android.os.Looper
import com.antsfamily.biketrainer.data.models.Profile
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
        Handler(Looper.getMainLooper()).postDelayed(::getSelectedProfile, 500L)
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
        data?.let {
            navigateToStart(it.getName()) } ?: navigateToCreateProfile()
    }

    private fun showLoading() {
        changeState { it.copy(isLoading = true) }
    }

    private fun navigateToStart(username: String) {
        navigateTo(SplashToHome(username))
        hideLoading()
    }

    private fun navigateToCreateProfile() {
        navigateTo(SplashToCreateProfile)
        hideLoading()
    }

    private fun hideLoading() {
        changeState { it.copy(isLoading = false) }
    }
}

package com.antsfamily.biketrainer.presentation.splash

import android.os.Handler
import android.os.Looper
import com.antsfamily.biketrainer.navigation.SplashToStart
import com.antsfamily.biketrainer.presentation.StatefulViewModel
import javax.inject.Inject

class SplashViewModel @Inject constructor(

) : StatefulViewModel<SplashViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = false
    )

    fun onResume() {
        showLoading()
        Handler(Looper.getMainLooper()).postDelayed(::navigateToStart, 1500L)
    }

    private fun showLoading() {
        changeState { it.copy(isLoading = true) }
    }
    private fun navigateToStart() {
        navigateTo(SplashToStart)
        hideLoading()
    }

    private fun hideLoading() {
        changeState { it.copy(isLoading = false) }
    }
}

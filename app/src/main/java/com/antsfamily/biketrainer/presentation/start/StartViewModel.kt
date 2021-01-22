package com.antsfamily.biketrainer.presentation.start

import com.antsfamily.biketrainer.presentation.BaseViewModel
import com.antsfamily.biketrainer.presentation.navigation.StartToProfile
import com.antsfamily.biketrainer.presentation.navigation.StartToPrograms
import javax.inject.Inject

class StartViewModel @Inject constructor() : BaseViewModel() {

    fun onStartClick() {
        // TODO: later
    }

    fun onProfileClick() {
        navigateTo(StartToProfile)
    }

    fun onProgramClick() {
        navigateTo(StartToPrograms)
    }
}

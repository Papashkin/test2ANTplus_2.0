package com.antsfamily.biketrainer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {
    companion object {
        const val ACTION_WORK_SENDING = "com.antsfamily.biketrainer.presentation.view.workScreen.WorkFragment"
        const val ACTION_PROGRAM_SETTINGS = "com.antsfamily.biketrainer.ui.programs.ProgramsFragment"
        const val UPD_PROGRAMS_LIST = "programs list"
        const val ARGS_PROGRAM = "selected program"

        const val PERMISSION_FOR_APP = 1

        var PROGRAM_IMAGES_PATH: String? = null
    }
}

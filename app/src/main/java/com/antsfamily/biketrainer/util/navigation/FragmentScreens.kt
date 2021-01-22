package com.antsfamily.biketrainer.util.navigation

import androidx.fragment.app.Fragment
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.antsfamily.biketrainer.data.models.Program
import com.antsfamily.biketrainer.ui.profiles.ProfilesFragment
import com.antsfamily.biketrainer.ui.settings.ProgramSettingsFragment
import com.antsfamily.biketrainer.ui.programs.ProgramsFragment
import com.antsfamily.biketrainer.ui.programs.ProgramsFragment.Companion.newInstance
import com.antsfamily.biketrainer.ui.scanning.ScanFragment
import com.antsfamily.biketrainer.ui.start.StartFragment
import com.antsfamily.biketrainer.ui.workout.WorkoutFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

class FragmentScreens {

    class ProfileScreen(private val isTime2work: Boolean = false) : SupportAppScreen() {
        override fun getFragment(): Fragment = ProfilesFragment()
        override fun getScreenKey(): String = "profile_fragment".hashCode().toString()
    }

    class ProgramScreen(private val isTime2work: Boolean = false, private val profile: String) : SupportAppScreen() {
        override fun getFragment(): Fragment = ProgramsFragment.newInstance(isTime2work, profile)
        override fun getScreenKey(): String = "program_fragment".hashCode().toString()
    }

    class ProgramSettingsScreen(private val program: Program?) : SupportAppScreen() {
        override fun getFragment(): Fragment = ProgramSettingsFragment.newInstance(program)
        override fun getScreenKey(): String = "program_settings_fragment".hashCode().toString()
    }

    class StartScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = StartFragment()
        override fun getScreenKey(): String = "start_fragment".hashCode().toString()
    }

    class ScanScreen(private val profileName: String, private val program: Program) : SupportAppScreen() {
        override fun getFragment(): Fragment = ScanFragment.newInstance(profileName, program)
        override fun getScreenKey(): String = "scan_fragment".hashCode().toString()
    }

    class WorkScreen(private val devices: ArrayList<MultiDeviceSearchResult>, private val program: String, private val profileName: String) : SupportAppScreen() {
        override fun getFragment(): Fragment = WorkoutFragment.newInstance(devices, program, profileName)
        override fun getScreenKey(): String = "work_fragment".hashCode().toString()
    }
}

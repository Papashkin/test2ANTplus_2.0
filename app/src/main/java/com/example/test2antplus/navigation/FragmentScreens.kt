package com.example.test2antplus.navigation

import androidx.fragment.app.Fragment
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult
import com.example.test2antplus.ui.view.*
import ru.terrakok.cicerone.android.support.SupportAppScreen

class FragmentScreens {

    class ProfileScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ProfileFragment()
        override fun getScreenKey(): String = "profile_fragment".hashCode().toString()
    }

    class ProgramScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ProgramFragment()
        override fun getScreenKey(): String = "program_fragment".hashCode().toString()
    }

    class ProgramSettingsScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ProgramSettingsFragment()
        override fun getScreenKey(): String = "program_settings_fragment".hashCode().toString()
    }

    class StartScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = StartFragment()
        override fun getScreenKey(): String = "start_fragment".hashCode().toString()
    }

    class ScanScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ScanFragment()
        override fun getScreenKey(): String = "scan_fragment".hashCode().toString()
    }

    class WorkScreen(private val devices: ArrayList<MultiDeviceSearchResult>) : SupportAppScreen() {
        override fun getFragment(): Fragment = WorkFragment().newInstance(devices)
        override fun getScreenKey(): String = "work_fragment".hashCode().toString()
    }
}
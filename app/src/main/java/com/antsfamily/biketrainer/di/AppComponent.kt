package com.antsfamily.biketrainer.di

import com.antsfamily.biketrainer.MainActivity
import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.di.modules.AppModule
import com.antsfamily.biketrainer.di.modules.NavigationModule
import com.antsfamily.biketrainer.presentation.profiles.ProfilesPresenter
import com.antsfamily.biketrainer.presentation.programSettings.ProgramSettingsPresenter
import com.antsfamily.biketrainer.presentation.programs.ProgramsPresenter
import com.antsfamily.biketrainer.presentation.scan.ScanPresenter
import com.antsfamily.biketrainer.presentation.start.StartPresenter
import com.antsfamily.biketrainer.presentation.work.WorkPresenter
import com.antsfamily.biketrainer.ui.profiles.ProfilesFragment
import com.antsfamily.biketrainer.ui.programsettings.ProgramSettingsFragment
import com.antsfamily.biketrainer.ui.programs.ProgramsFragment
import com.antsfamily.biketrainer.ui.scan.ScanFragment
import com.antsfamily.biketrainer.ui.start.StartFragment
import com.antsfamily.biketrainer.ui.work.WorkFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationModule::class,
        AppModule::class
    ]
)
interface AppComponent {
    fun inject(mainApplication: MainApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(scanFragment: ScanFragment)
    fun inject(scanPresenter: ScanPresenter)
    fun inject(workFragment: WorkFragment)
    fun inject(workPresenter: WorkPresenter)
    fun inject(profilesFragment: ProfilesFragment)
    fun inject(profilesPresenter: ProfilesPresenter)
    fun inject(programSettingsFragment: ProgramSettingsFragment)
    fun inject(programSettingsPresenter: ProgramSettingsPresenter)
    fun inject(startFragment: StartFragment)
    fun inject(startPresenter: StartPresenter)
    fun inject(programsFragment: ProgramsFragment)
    fun inject(programsPresenter: ProgramsPresenter)
}
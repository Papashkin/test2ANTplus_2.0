package com.example.test2antplus.di

import com.example.test2antplus.MainActivity
import com.example.test2antplus.MainApplication
import com.example.test2antplus.di.modules.AppModule
import com.example.test2antplus.di.modules.NavigationModule
import com.example.test2antplus.presentation.presenter.profiles.ProfilesPresenter
import com.example.test2antplus.presentation.presenter.programSettings.ProgramSettingsPresenter
import com.example.test2antplus.presentation.presenter.programs.ProgramsPresenter
import com.example.test2antplus.presentation.presenter.scan.ScanPresenter
import com.example.test2antplus.presentation.presenter.start.StartPresenter
import com.example.test2antplus.presentation.presenter.work.WorkPresenter
import com.example.test2antplus.presentation.view.profiles.ProfilesFragment
import com.example.test2antplus.presentation.view.programSettings.ProgramSettingsFragment
import com.example.test2antplus.presentation.view.programs.ProgramsFragment
import com.example.test2antplus.presentation.view.scan.ScanFragment
import com.example.test2antplus.presentation.view.start.StartFragment
import com.example.test2antplus.presentation.view.work.WorkFragment
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
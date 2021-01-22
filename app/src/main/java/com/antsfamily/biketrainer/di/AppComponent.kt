package com.antsfamily.biketrainer.di

import com.antsfamily.biketrainer.MainActivity
import com.antsfamily.biketrainer.MainApplication
import com.antsfamily.biketrainer.di.modules.AppModule
import com.antsfamily.biketrainer.di.modules.ViewModelsModule
import com.antsfamily.biketrainer.presentation.ViewModelFactory
import com.antsfamily.biketrainer.ui.profiles.ProfilesFragment
import com.antsfamily.biketrainer.ui.programs.ProgramsFragment
import com.antsfamily.biketrainer.ui.scanning.ScanFragment
import com.antsfamily.biketrainer.ui.settings.ProgramSettingsFragment
import com.antsfamily.biketrainer.ui.start.StartFragment
import com.antsfamily.biketrainer.ui.workout.WorkoutFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ViewModelsModule::class])
interface AppComponent {
    fun genericSavedStateViewModelFactory(): ViewModelFactory

    fun inject(mainApplication: MainApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(profilesFragment: ProfilesFragment)
    fun inject(programsFragment: ProgramsFragment)
    fun inject(programSettingsFragment: ProgramSettingsFragment)
    fun inject(scanFragment: ScanFragment)
    fun inject(startFragment: StartFragment)
    fun inject(workoutFragment: WorkoutFragment)
}

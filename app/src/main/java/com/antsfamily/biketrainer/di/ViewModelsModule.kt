package com.antsfamily.biketrainer.di

import androidx.lifecycle.ViewModel
import com.antsfamily.biketrainer.presentation.profiles.ProfilesViewModel
import com.antsfamily.biketrainer.presentation.programSettings.ProgramSettingsViewModel
import com.antsfamily.biketrainer.presentation.programs.ProgramsViewModel
import com.antsfamily.biketrainer.presentation.scan.ScanViewModel
import com.antsfamily.biketrainer.presentation.start.StartViewModel
import com.antsfamily.biketrainer.presentation.workout.WorkoutViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfilesViewModel::class)
    abstract fun bindProfilesViewModel(viewModel: ProfilesViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProgramsViewModel::class)
    abstract fun bindProgramsViewModel(viewModel: ProgramsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProgramSettingsViewModel::class)
    abstract fun bindProgramSettingsViewModel(viewModel: ProgramSettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScanViewModel::class)
    abstract fun bindScanViewModel(viewModel: ScanViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StartViewModel::class)
    abstract fun bindStartViewModel(viewModel: StartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WorkoutViewModel::class)
    abstract fun bindWorkViewModel(viewModel: WorkoutViewModel): ViewModel
}

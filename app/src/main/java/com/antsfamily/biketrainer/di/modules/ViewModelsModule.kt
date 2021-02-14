package com.antsfamily.biketrainer.di.modules

import androidx.lifecycle.ViewModel
import com.antsfamily.biketrainer.di.ViewModelKey
import com.antsfamily.biketrainer.presentation.createprofile.CreateProfileViewModel
import com.antsfamily.biketrainer.presentation.createprogram.AddIntervalBottomSheetViewModel
import com.antsfamily.biketrainer.presentation.createprogram.AddSegmentBottomSheetViewModel
import com.antsfamily.biketrainer.presentation.createprogram.AddStairsBottomSheetViewModel
import com.antsfamily.biketrainer.presentation.profiles.ProfilesViewModel
import com.antsfamily.biketrainer.presentation.createprogram.CreateProgramViewModel
import com.antsfamily.biketrainer.presentation.programs.ProgramsViewModel
import com.antsfamily.biketrainer.presentation.scan.ScanViewModel
import com.antsfamily.biketrainer.presentation.splash.SplashViewModel
import com.antsfamily.biketrainer.presentation.start.StartViewModel
import com.antsfamily.biketrainer.presentation.workout.WorkoutViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap

@InstallIn(ApplicationComponent::class)
@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel) : ViewModel

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
    @ViewModelKey(CreateProfileViewModel::class)
    abstract fun bindCreateProfileViewModel(viewModel: CreateProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateProgramViewModel::class)
    abstract fun bindProgramSettingsViewModel(viewModel: CreateProgramViewModel): ViewModel

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

    @Binds
    @IntoMap
    @ViewModelKey(AddSegmentBottomSheetViewModel::class)
    abstract fun bindAddSegmentBottomSheetViewModel(viewModel: AddSegmentBottomSheetViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddIntervalBottomSheetViewModel::class)
    abstract fun bindAddIntervalBottomSheetViewModel(viewModel: AddIntervalBottomSheetViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddStairsBottomSheetViewModel::class)
    abstract fun bindAddStairsBottomSheetViewModel(viewModel: AddStairsBottomSheetViewModel): ViewModel
}

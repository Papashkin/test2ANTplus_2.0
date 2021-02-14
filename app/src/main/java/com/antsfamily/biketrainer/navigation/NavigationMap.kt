package com.antsfamily.biketrainer.navigation

import androidx.navigation.NavDirections
import com.antsfamily.biketrainer.ui.createprofile.CreateProfileFragmentDirections
import com.antsfamily.biketrainer.ui.createprogram.CreateProgramFragmentDirections
import com.antsfamily.biketrainer.ui.programs.ProgramsFragmentDirections
import com.antsfamily.biketrainer.ui.scanning.ScanFragmentDirections
import com.antsfamily.biketrainer.ui.splash.SplashFragmentDirections
import com.antsfamily.biketrainer.ui.start.StartFragmentDirections

fun Route.mapToDirection(): NavDirections = when (this) {
    is SplashToStart -> SplashFragmentDirections.actionStartFragmentToStartFragment()
    is SplashToCreateProfile -> SplashFragmentDirections.actionStartFragmentToCreateProfileFragment()
    is CreateProfileToStart -> CreateProfileFragmentDirections.actionCreateProfileFragmentToStartFragment()
    is StartToProfile -> StartFragmentDirections.actionStartFragmentToProfilesFragment()
    is StartToPrograms -> StartFragmentDirections.actionStartFragmentToProgramsFragment()
    is ProgramToCreateProgram -> ProgramsFragmentDirections.actionProgramsFragmentToCreateProgramFragment()
    is ScanToWorkout -> ScanFragmentDirections.actionScanFragmentToWorkoutFragment(
        devices.toTypedArray(), program, profile
    )
    is CreateProgramToAddSegment -> CreateProgramFragmentDirections.actionCreateProgramFragmentToAddSegmentBottomSheetDialogFragment()
    is CreateProgramToAddInterval -> CreateProgramFragmentDirections.actionCreateProgramFragmentToAddIntervalBottomSheetDialogFragment()
    is CreateProgramToAddStairs -> CreateProgramFragmentDirections.actionCreateProgramFragmentToAddStairsBottomSheetDialogFragment(
        type
    )
}

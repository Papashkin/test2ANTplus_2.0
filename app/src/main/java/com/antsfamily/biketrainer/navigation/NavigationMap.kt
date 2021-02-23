package com.antsfamily.biketrainer.navigation

import androidx.navigation.NavDirections
import com.antsfamily.biketrainer.ui.createprofile.CreateProfileFragmentDirections
import com.antsfamily.biketrainer.ui.createprogram.CreateProgramFragmentDirections
import com.antsfamily.biketrainer.ui.scanning.ScanFragmentDirections
import com.antsfamily.biketrainer.ui.splash.SplashFragmentDirections
import com.antsfamily.biketrainer.ui.home.HomeFragmentDirections
import com.antsfamily.biketrainer.ui.programinfo.ProgramInfoFragmentDirections

fun Route.mapToDirection(): NavDirections = when (this) {
    is SplashToHome -> SplashFragmentDirections.actionStartFragmentToStartFragment()
    is SplashToCreateProfile -> SplashFragmentDirections.actionStartFragmentToCreateProfileFragment()
    is CreateProfileToHome -> CreateProfileFragmentDirections.actionCreateProfileFragmentToStartFragment()
    is HomeToProfile -> HomeFragmentDirections.actionStartFragmentToProfilesFragment()
    is HomeToCreateProgram -> HomeFragmentDirections.actionHomeFragmentToCreateProgramFragment()
    is HomeToProgramInfo -> HomeFragmentDirections.actionHomeFragmentToProgramInfoFragment(
        programName
    )
    is ProgramInfoToScan -> ProgramInfoFragmentDirections.actionProgramInfoFragmentToScanFragment(
        programName
    )
    is ScanToWorkout -> ScanFragmentDirections.actionScanFragmentToWorkoutFragment(
        devices.toTypedArray(), program, profile
    )
    is CreateProgramToAddSegment -> CreateProgramFragmentDirections.actionCreateProgramFragmentToAddSegmentBottomSheetDialogFragment()
    is CreateProgramToAddInterval -> CreateProgramFragmentDirections.actionCreateProgramFragmentToAddIntervalBottomSheetDialogFragment()
    is CreateProgramToAddStairs -> CreateProgramFragmentDirections.actionCreateProgramFragmentToAddStairsBottomSheetDialogFragment()
}

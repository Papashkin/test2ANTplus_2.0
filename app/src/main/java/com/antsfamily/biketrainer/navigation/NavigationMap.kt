package com.antsfamily.biketrainer.navigation

import androidx.navigation.NavDirections
import com.antsfamily.biketrainer.ui.createprofile.CreateProfileFragmentDirections
import com.antsfamily.biketrainer.ui.createprogram.CreateProgramFragmentDirections
import com.antsfamily.biketrainer.ui.scanning.ScanFragmentDirections
import com.antsfamily.biketrainer.ui.splash.SplashFragmentDirections
import com.antsfamily.biketrainer.ui.home.HomeFragmentDirections

fun Route.mapToDirection(): NavDirections = when (this) {
    is SplashToHome -> SplashFragmentDirections.actionStartFragmentToStartFragment(username)
    is SplashToCreateProfile -> SplashFragmentDirections.actionStartFragmentToCreateProfileFragment()
    is CreateProfileToHome -> CreateProfileFragmentDirections.actionCreateProfileFragmentToStartFragment(username)
    is HomeToProfile -> HomeFragmentDirections.actionStartFragmentToProfilesFragment()
    is HomeToCreateProgram -> HomeFragmentDirections.actionHomeFragmentToCreateProgramFragment()
    is ScanToWorkout -> ScanFragmentDirections.actionScanFragmentToWorkoutFragment(
        devices.toTypedArray(), program, profile
    )
    is CreateProgramToAddSegment -> CreateProgramFragmentDirections.actionCreateProgramFragmentToAddSegmentBottomSheetDialogFragment()
    is CreateProgramToAddInterval -> CreateProgramFragmentDirections.actionCreateProgramFragmentToAddIntervalBottomSheetDialogFragment()
    is CreateProgramToAddStairs -> CreateProgramFragmentDirections.actionCreateProgramFragmentToAddStairsBottomSheetDialogFragment(
        type
    )
}

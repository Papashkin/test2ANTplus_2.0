package com.antsfamily.biketrainer.navigation

import androidx.navigation.NavDirections
import com.antsfamily.biketrainer.presentation.navigation.*
import com.antsfamily.biketrainer.ui.programs.ProgramsFragmentDirections
import com.antsfamily.biketrainer.ui.scanning.ScanFragmentDirections
import com.antsfamily.biketrainer.ui.start.StartFragmentDirections

fun Route.mapToDirection(): NavDirections = when (this) {
    is StartToProfile -> StartFragmentDirections.actionStartFragmentToProfilesFragment()
    is StartToPrograms -> StartFragmentDirections.actionStartFragmentToProgramsFragment()
    is ProgramToProgramSettings -> ProgramsFragmentDirections.actionProgramsFragmentToProgramSettingsFragment(
        programId
    )
    ProgramToScan -> ProgramsFragmentDirections.actionProgramsFragmentToScanFragment()
    ScanToWorkout -> ScanFragmentDirections.actionScanFragmentToWorkoutFragment()
}

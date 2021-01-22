package com.antsfamily.biketrainer.presentation.navigation

sealed class Route
object StartToProfile : Route()
object StartToPrograms : Route()
class ProgramToProgramSettings(val programId: Int) : Route()
object ProgramToScan : Route()
object ScanToWorkout : Route()

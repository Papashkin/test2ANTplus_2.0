package com.antsfamily.biketrainer.navigation

import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch

sealed class Route
object StartToProfile : Route()
object StartToPrograms : Route()
class ProgramToProgramSettings(val programId: String?) : Route()
object ProgramToScan : Route()
class ScanToWorkout(
    val devices: List<MultiDeviceSearch.MultiDeviceSearchResult>,
    val program: String,
    val profile: String
) : Route()
object ProfileToCreateProfile: Route()

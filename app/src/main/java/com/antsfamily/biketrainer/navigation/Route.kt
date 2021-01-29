package com.antsfamily.biketrainer.navigation

import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch

sealed class Route
object StartToProfile : Route()
object StartToPrograms : Route()
object ProgramToCreateProgram : Route()
class ScanToWorkout(
    val devices: List<MultiDeviceSearch.MultiDeviceSearchResult>,
    val program: String,
    val profile: String
) : Route()
object ProfileToCreateProfile: Route()

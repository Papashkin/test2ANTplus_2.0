package com.antsfamily.biketrainer.navigation

import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch

sealed class Route
object SplashToHome : Route()
object SplashToCreateProfile : Route()
object CreateProfileToHome: Route()
object HomeToProfile : Route()
object HomeToCreateProgram : Route()
class HomeToProgramInfo(val programName: String) : Route()
class ProgramInfoToScan(val programName: String) : Route()

class ScanToWorkout(
    val devices: List<MultiDeviceSearch.MultiDeviceSearchResult>,
    val program: String,
    val profile: String
) : Route()

object CreateProgramToAddSegment: Route()
object CreateProgramToAddInterval: Route()
object CreateProgramToAddStairs: Route()

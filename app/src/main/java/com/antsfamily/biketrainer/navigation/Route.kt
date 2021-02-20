package com.antsfamily.biketrainer.navigation

import com.antsfamily.biketrainer.data.models.program.ProgramType
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch

sealed class Route
object SplashToHome : Route()
object SplashToCreateProfile : Route()
object CreateProfileToHome: Route()
object HomeToProfile : Route()
object HomeToCreateProgram : Route()

class ScanToWorkout(
    val devices: List<MultiDeviceSearch.MultiDeviceSearchResult>,
    val program: String,
    val profile: String
) : Route()

object CreateProgramToAddSegment: Route()
object CreateProgramToAddInterval: Route()
object CreateProgramToAddStairs: Route()

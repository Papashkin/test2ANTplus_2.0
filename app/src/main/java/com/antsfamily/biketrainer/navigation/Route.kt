package com.antsfamily.biketrainer.navigation

import com.antsfamily.biketrainer.data.models.ProgramType
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch

sealed class Route
class SplashToHome(val username: String) : Route()
object SplashToCreateProfile : Route()
class CreateProfileToHome(val username: String): Route()
object HomeToProfile : Route()
object HomeToCreateProgram : Route()

class ScanToWorkout(
    val devices: List<MultiDeviceSearch.MultiDeviceSearchResult>,
    val program: String,
    val profile: String
) : Route()

//object ProgramToCreateProgram : Route()
object CreateProgramToAddSegment: Route()
object CreateProgramToAddInterval: Route()
class CreateProgramToAddStairs(val type: ProgramType): Route()

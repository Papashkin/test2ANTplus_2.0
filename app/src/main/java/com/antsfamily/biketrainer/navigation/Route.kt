package com.antsfamily.biketrainer.navigation

import com.antsfamily.biketrainer.data.models.ProgramType
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch

sealed class Route
object SplashToStart : Route()
object StartToProfile : Route()
object StartToPrograms : Route()

object ProfileToCreateProfile: Route()

class ScanToWorkout(
    val devices: List<MultiDeviceSearch.MultiDeviceSearchResult>,
    val program: String,
    val profile: String
) : Route()

object ProgramToCreateProgram : Route()
object CreateProgramToAddSegment: Route()
object CreateProgramToAddInterval: Route()
class CreateProgramToAddStairs(val type: ProgramType): Route()

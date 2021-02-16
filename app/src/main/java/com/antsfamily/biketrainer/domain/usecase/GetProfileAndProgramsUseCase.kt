package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import com.antsfamily.biketrainer.data.models.Profile
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class GetProfileAndProgramsUseCase @Inject constructor(
    private val profilesRepository: ProfilesRepository,
    private val programsRepository: ProgramsRepository
) : BaseUseCase<String, Result<Pair<Profile, List<Program>>, Error>>() {

    override suspend fun run(params: String): Result<Pair<Profile, List<Program>>, Error> = try {
        val profile = profilesRepository.getSelectedProfile()
        val programs = programsRepository.getProgramsByUsername(params)
        profile?.let {
            Result.Success(it to programs)
        } ?: Result.Failure(Error("Epic fail :("))
    } catch (e: Exception) {
        Result.Failure(Error(e.message ?: "Epic fail :("))
    }
}

package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.data.models.profile.ProfileWithPrograms
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class GetProfileAndProgramsUseCase @Inject constructor(
    private val profilesRepository: ProfilesRepository
) : BaseUseCase<Unit, Result<ProfileWithPrograms, Error>>() {

    override suspend fun run(params: Unit) = try {
        val profileAnPrograms = profilesRepository.getSelectedProfileWithPrograms()
        Result.Success(profileAnPrograms)
    } catch (e: Exception) {
        Result.Failure(Error(e.message ?: "Epic fail :("))
    }
}

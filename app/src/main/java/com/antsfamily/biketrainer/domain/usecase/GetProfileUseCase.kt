package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.data.models.Profile
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfilesRepository
) : BaseUseCase<Unit, List<Profile>>() {

    override suspend fun run(params: Unit): Result<List<Profile>, Error> = try {
            val profiles = profileRepository.getAllProfiles()
            Result.Success(profiles)
        } catch (e: Exception) {
            Result.Failure(Error("Epic fail"))
        }
}

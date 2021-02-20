package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.data.models.profile.Profile
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class GetSelectedProfileUseCase @Inject constructor(
    private val profileRepository: ProfilesRepository
) : BaseUseCase<Unit, Result<Profile, Error>>() {

    override suspend fun run(params: Unit): Result<Profile, Error> = try {
        profileRepository.getSelectedProfile()?.let {
            Result.Success(it)
        } ?: Result.Failure(Error("There is no selected profile"))
    } catch (e: Exception) {
        Result.Failure(Error("Epic fail"))
    }
}

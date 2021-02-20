package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.data.models.profile.Profile
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class CreateProfileUseCase @Inject constructor(
    private val profileRepository: ProfilesRepository
) : BaseUseCase<Profile, Result<Unit, Error>>() {

    override suspend fun run(params: Profile): Result<Unit, Error> = try {
        profileRepository.clearSelectedProfile()
        Result.Success(profileRepository.insertProfile(params))
    } catch (e: Exception) {
        Result.Failure(Error("Epic fail"))
    }
}

package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class ClearSelectedProfilesUseCase @Inject constructor(
    private val repository: ProfilesRepository
) : BaseUseCase<Unit, Result<Unit, Error>>() {

    override suspend fun run(params: Unit): Result<Unit, Error> = try {
        Result.Success(repository.clearSelectedProfile())
    } catch (e: Exception) {
        Result.Failure(Error(e.message ?: "Epic fail"))
    }
}

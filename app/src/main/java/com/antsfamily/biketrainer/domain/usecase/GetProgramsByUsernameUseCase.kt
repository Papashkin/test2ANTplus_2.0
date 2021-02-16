package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class GetProgramsByUsernameUseCase @Inject constructor(
    private val programsRepository: ProgramsRepository
) : BaseUseCase<String, Result<List<Program>, Error>>() {

    override suspend fun run(params: String): Result<List<Program>, Error> = try {
        Result.Success(programsRepository.getProgramsByUsername(params))
    } catch (e: Exception) {
        Result.Failure(Error(e.message ?: "Epic fail :("))
    }
}

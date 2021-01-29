package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import com.antsfamily.biketrainer.data.models.Program
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class GetProgramsUseCase @Inject constructor(
    private val programsRepository: ProgramsRepository
) : BaseUseCase<Unit, Result<List<Program>, Error>>() {

    override suspend fun run(params: Unit): Result<List<Program>, Error> = try {
        Result.Success(programsRepository.getAllPrograms())
    } catch (e: Exception) {
        Result.Failure(Error("Epic fail"))
    }
}

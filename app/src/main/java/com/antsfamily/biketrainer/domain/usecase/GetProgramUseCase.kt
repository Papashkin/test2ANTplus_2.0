package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class GetProgramUseCase @Inject constructor(
    private val programsRepository: ProgramsRepository
) : BaseUseCase<String, Result<Program, Error>>() {

    override suspend fun run(params: String): Result<Program, Error> = try {
        programsRepository.getProgram(params)?.let {
            Result.Success(it)
        } ?: Result.Failure(Error("There is no program with mentioned title"))
    } catch (e: Exception) {
        Result.Failure(Error("Epic fail"))
    }
}

package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class SaveProgramUseCase @Inject constructor(
    private val programsRepository: ProgramsRepository
) : BaseUseCase<Program, Result<Unit, Error>>() {

    override suspend fun run(params: Program): Result<Unit, Error> = try {
        Result.Success(programsRepository.insertProgram(params))
    } catch (e: Exception) {
        Result.Failure(Error("Epic fail :("))
    }
}

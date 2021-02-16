package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.data.local.repositories.ProfilesRepository
import com.antsfamily.biketrainer.data.local.repositories.ProgramsRepository
import com.antsfamily.biketrainer.data.models.program.Program
import com.antsfamily.biketrainer.data.models.program.ProgramData
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class SaveProgramUseCase @Inject constructor(
    private val profilesRepository: ProfilesRepository,
    private val programsRepository: ProgramsRepository
) : BaseUseCase<SaveProgramUseCase.Params, Result<Unit, Error>>() {

    override suspend fun run(params: Params): Result<Unit, Error> = try {
        profilesRepository.getSelectedProfile()?.let {
            val program = Program(
                id = params.id,
                name = params.name,
                data = params.data,
                username = it.getName()
            )
            Result.Success(programsRepository.insertProgram(program))
        } ?: Result.Failure(Error("Selected profile is absent"))
    } catch (e: Exception) {
        Result.Failure(Error("Epic fail :("))
    }

    data class Params(
        val id: Int,
        val name: String,
        val data: List<ProgramData>
    )
}

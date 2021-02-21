package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.ant.service.AntRadioServiceConnection
import com.antsfamily.biketrainer.domain.BaseUseCase
import com.antsfamily.biketrainer.domain.Result
import javax.inject.Inject

class UnbindAntChannelUseCase @Inject constructor(
    private val connection: AntRadioServiceConnection
) : BaseUseCase<Unit, Result<Unit, Error>>() {
    override suspend fun run(params: Unit): Result<Unit, Error> = try {
        Result.Success(connection.closeBackgroundScanChannel())
    } catch (e: Exception) {
        Result.Failure(Error(e.message ?: "Epic fail :("))
    }
}

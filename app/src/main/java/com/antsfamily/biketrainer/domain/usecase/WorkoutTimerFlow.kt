package com.antsfamily.biketrainer.domain.usecase

import com.antsfamily.biketrainer.domain.FlowUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WorkoutTimerFlow @Inject constructor() : FlowUseCase<Long, Unit>() {

    override fun run(params: Long): Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(params)
        }
    }
}

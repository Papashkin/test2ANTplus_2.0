package com.antsfamily.biketrainer.data.local.repositories

import androidx.room.*
import com.antsfamily.biketrainer.data.local.database.ProgramDao
import com.antsfamily.biketrainer.data.models.program.Program
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramsRepository @Inject constructor(private val dao: ProgramDao) {
    suspend fun getAllPrograms(): List<Program> = dao.getAll()
    val programs: Flow<List<Program>> = dao.getPrograms()
    suspend fun getProgram(name: String): Program? = dao.getProgram(name)
    suspend fun getProgramsByUsername(username: String): List<Program> =
        dao.getProgramsByUsername(username)
    suspend fun insertProgram(program: Program) = dao.insertProgram(program)
    suspend fun updateProgram(program: Program) = dao.updateProgram(program)
    suspend fun removeProgram(program: Program) = dao.deleteProgram(program)
}

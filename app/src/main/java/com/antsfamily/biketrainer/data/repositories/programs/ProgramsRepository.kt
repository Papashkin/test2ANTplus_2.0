package com.antsfamily.biketrainer.data.repositories.programs

import com.antsfamily.biketrainer.data.db.programs.ProgramsDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramsRepository @Inject constructor(private val programsDao: ProgramsDao) {

    suspend fun getAllPrograms(): List<Program> = programsDao.getAll()
    suspend fun getProgramByName(name: String): Program? = programsDao.getProgram(name)
    suspend fun getProgramIdByName(name: String): Int = programsDao.getProgramId(name)
    suspend fun insertProgram(program: Program) = programsDao.addProgram(program)
    suspend fun updateProgram(program: Program) = programsDao.updateProgram(program)
    suspend fun removeProgram(program: Program) = programsDao.deleteProgram(program)
}
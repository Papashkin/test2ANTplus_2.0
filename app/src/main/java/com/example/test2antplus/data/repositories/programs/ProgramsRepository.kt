package com.example.test2antplus.data.repositories.programs

import com.example.test2antplus.data.db.programs.ProgramsDao
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramsRepository @Inject constructor(private val programsDao: ProgramsDao) {

    fun getAllPrograms(): Single<List<Program>> = programsDao.getAll()

    fun getProgramByName(name: String): Single<Program> = programsDao.getProgram(name)


    fun getProgramIdByName(name: String): Single<Int> = programsDao.getProgramId(name)

    fun insertProgram(program: Program) {
        programsDao.addProgram(program)
    }

    fun updateProgram(program: Program) {
        programsDao.updateProgram(program)
    }

    fun removeProgram(program: Program) {
        programsDao.deleteProgram(program)
    }
}
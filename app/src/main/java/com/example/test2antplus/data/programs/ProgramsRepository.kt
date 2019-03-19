package com.example.test2antplus.data.programs

import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramsRepository @Inject constructor(private val programDao: ProgramDao) {

    fun getAllPrograms(): Flowable<List<Program>> = programDao.getAll()

    fun getProgramByName(name: String): Single<Program> = programDao.getProgram(name)

    fun insertProgram(program: Program) {
        programDao.addProgram(program)
    }

    fun removeProgram(program: Program) {
        programDao.deleteProgram(program)
    }
}
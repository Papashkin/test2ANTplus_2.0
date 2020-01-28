package com.antsfamily.biketrainer.data.local.repositories

import androidx.lifecycle.LiveData
import androidx.room.*
import com.antsfamily.biketrainer.data.models.Program
import javax.inject.Inject
import javax.inject.Singleton

@Dao
interface ProgramsDao {
    @Query("SELECT * from program")
    fun getAll(): LiveData<List<Program>>

    @Query("Select * from program where name = :programName")
    suspend fun getProgram(programName: String): Program?

    @Query("Select id from program where name = :programName")
    suspend fun getProgramId(programName: String): Int

    @Insert
    suspend fun addProgram(profile: Program)

    @Update
    suspend fun updateProgram(program: Program)

    @Delete
    suspend fun deleteProgram(profile: Program)
}

@Singleton
class ProgramsRepository @Inject constructor(private val programsDao: ProgramsDao) {
    fun getAllPrograms(): LiveData<List<Program>> = programsDao.getAll()
    suspend fun getProgramByName(name: String): Program? = programsDao.getProgram(name)
    suspend fun getProgramIdByName(name: String): Int = programsDao.getProgramId(name)
    suspend fun insertProgram(program: Program) = programsDao.addProgram(program)
    suspend fun updateProgram(program: Program) = programsDao.updateProgram(program)
    suspend fun removeProgram(program: Program) = programsDao.deleteProgram(program)
}

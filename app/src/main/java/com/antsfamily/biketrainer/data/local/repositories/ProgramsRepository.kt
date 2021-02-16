package com.antsfamily.biketrainer.data.local.repositories

import androidx.room.*
import com.antsfamily.biketrainer.data.models.program.Program
import javax.inject.Inject
import javax.inject.Singleton

@Dao
interface ProgramsDao {
    @Query("SELECT * from program")
    suspend fun getAll(): List<Program>

    @Query("Select * from program where name = :name")
    suspend fun getProgram(name: String): Program?

    @Query("Select * from program where username = :username")
    suspend fun getProgramsByUsername(username: String): List<Program>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProgram(profile: Program)

    @Update
    suspend fun updateProgram(program: Program)

    @Delete
    suspend fun deleteProgram(profile: Program)
}

@Singleton
class ProgramsRepository @Inject constructor(private val programsDao: ProgramsDao) {
    suspend fun getAllPrograms(): List<Program> = programsDao.getAll()
    suspend fun getProgram(name: String): Program? = programsDao.getProgram(name)
    suspend fun getProgramsByUsername(username: String): List<Program> =
        programsDao.getProgramsByUsername(username)

    suspend fun insertProgram(program: Program) = programsDao.insertProgram(program)
    suspend fun updateProgram(program: Program) = programsDao.updateProgram(program)
    suspend fun removeProgram(program: Program) = programsDao.deleteProgram(program)
}

package com.antsfamily.biketrainer.data.db.programs

import androidx.room.*
import com.antsfamily.biketrainer.data.repositories.programs.Program

@Dao
interface ProgramsDao {
    @Query("SELECT * from program")
    suspend fun getAll(): List<Program>

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
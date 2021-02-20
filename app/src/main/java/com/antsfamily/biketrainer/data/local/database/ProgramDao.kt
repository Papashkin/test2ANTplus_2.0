package com.antsfamily.biketrainer.data.local.database

import androidx.room.*
import com.antsfamily.biketrainer.data.models.program.Program

@Dao
abstract class ProgramDao {
    @Query("SELECT * from program")
    abstract suspend fun getAll(): List<Program>

    @Query("Select * from program where title = :title")
    abstract suspend fun getProgram(title: String): Program?

    @Query("Select * from program where username = :username")
    abstract suspend fun getProgramsByUsername(username: String): List<Program>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertProgram(profile: Program)

    @Update
    abstract suspend fun updateProgram(program: Program)

    @Delete
    abstract suspend fun deleteProgram(profile: Program)
}

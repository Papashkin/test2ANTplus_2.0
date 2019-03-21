package com.example.test2antplus.data.programs

import androidx.room.*
import io.reactivex.Single

@Dao
interface ProgramDao {
    @Query("SELECT * from program")
    fun getAll(): Single<List<Program>>

    @Query("Select * from program where name = :programName")
    fun getProgram(programName: String): Single<Program>

    @Insert
    fun addProgram(profile: Program)

    @Update
    fun updateProgram(program: Program)

    @Delete
    fun deleteProgram(profile: Program)

}
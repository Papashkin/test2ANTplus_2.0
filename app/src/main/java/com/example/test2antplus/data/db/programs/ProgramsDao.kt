package com.example.test2antplus.data.db.programs

import androidx.room.*
import com.example.test2antplus.data.repositories.programs.Program
import io.reactivex.Single

@Dao
interface ProgramsDao {
    @Query("SELECT * from program")
    fun getAll(): Single<List<Program>>

    @Query("Select * from program where name = :programName")
    fun getProgram(programName: String): Single<Program>

    @Query("Select id from program where name = :programName")
    fun getProgramId(programName: String): Single<Int>

    @Insert
    fun addProgram(profile: Program)

    @Update
    fun updateProgram(program: Program)

    @Delete
    fun deleteProgram(profile: Program)

}
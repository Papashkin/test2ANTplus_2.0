package com.example.test2antplus.data.programs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.test2antplus.Program
import io.reactivex.Single

@Dao
interface ProgramDao {
    @Query("SELECT * from program")
    fun getAll(): List<Program>

    @Query("Select * from program where name = :programName")
    fun getProgram(programName: String): Single<Program>

    @Insert
    fun addProgram(profile: Program)

    @Delete
    fun deleteProgram(profile: Program)

}
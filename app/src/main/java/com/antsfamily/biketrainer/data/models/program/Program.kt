package com.antsfamily.biketrainer.data.models.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [Program] - data set class for trainings program
 * @param name - name of program (primary key)
 * @param data - List of [ProgramData], each of them contains power and duration
 * @param username - name of creator/main profile
 */
@Entity
data class Program(
    @PrimaryKey(autoGenerate = true)
    private val id: Int,

    @ColumnInfo(name = "name")
    private val name: String,

    @ColumnInfo(name = "program_data")
    private val data: List<ProgramData>,

    @ColumnInfo(name = "username")
    private val username: String
) {
    fun getId() = id

    fun getName() = name

    fun getData() = data

    fun getUsername() = username

    fun setName(name: String) {
        this.copy(name = name)
    }

    fun setData(data: List<ProgramData>) {
        this.copy(data = data)
    }

    fun setUsername(username: String) {
        this.copy(username = username)
    }
}

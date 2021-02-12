package com.antsfamily.biketrainer.data.models.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [Program] - data set class for trainings program
 * @param name - name of program (primary key)
 * @param data - List of [ProgramData], each of them contains power and duration
 */
@Entity
data class Program(
    @PrimaryKey(autoGenerate = true)
    private val id: Int,

    @ColumnInfo(name = "name")
    private val name: String,

    @ColumnInfo(name = "program_data")
    private val data: List<ProgramData>
) {
    fun getId() = this.id

    fun getName() = this.name

    fun getData() = this.data

    fun setName(name: String) {
        this.copy(name = name)
    }

    fun setData(data: List<ProgramData>) {
        this.copy(data = data)
    }
}

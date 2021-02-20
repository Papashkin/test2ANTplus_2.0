package com.antsfamily.biketrainer.data.models.program

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
    @PrimaryKey val title: String,
    val data: List<ProgramData>,
    val username: String
)

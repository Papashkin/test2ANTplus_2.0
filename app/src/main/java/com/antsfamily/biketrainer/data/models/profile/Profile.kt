package com.antsfamily.biketrainer.data.models.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.inject.Inject

/**
 * [Profile] - data class with information about user
 * @param name - user name;
 * @param age - user age;
 * @param gender - Male or Female;
 * @param weight - user weight;
 * @param height - user height;
 * @param isSelected - Flag shows is it main profile or not;
 */
@Entity
class Profile @Inject constructor(
    @PrimaryKey var name: String,
    var age: Int,
    var gender: String,
    var weight: Float,
    var height: Float,
    var isSelected: Boolean
)

package com.example.test2antplus

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.inject.Inject

/**
 * [Profile] - data class with information about user (stored in BD)
 * @param name - user name;
 * @param age - user age;
 * @param gender - Male or Female;
 * @param weight - user weight;
 * @param height - user height
 */
@Entity
class Profile @Inject constructor(
    @PrimaryKey (autoGenerate = true)
    private var id: Int,

    @ColumnInfo(name = "name")
    private var name: String,

    @ColumnInfo(name = "age")
    private var age: Int,

    @ColumnInfo(name = "gender")
    private var gender: String,

    @ColumnInfo(name = "weight")
    private var weight: Float,

    @ColumnInfo(name = "height")
    private var height: Float
) {
    fun getId() = this.id
    fun getName() = this.name
    fun getAge() = this.age
    fun getGender() = this.gender
    fun getWeight() = this.weight
    fun getHeight() = this.height

    fun setName(newName: String) {
        this.name = newName
    }

    fun setAge(newAge: Int) {
        this.age = newAge
    }

    fun setGender(newGender: String) {
        this.gender = newGender
    }

    fun setWeight(newWeight: Float) {
        this.weight = newWeight
    }

    fun setHeight(newHeight: Float) {
        this.height = newHeight
    }

}
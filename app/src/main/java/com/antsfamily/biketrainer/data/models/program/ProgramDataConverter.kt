package com.antsfamily.biketrainer.data.models.program

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ProgramDataConverter {
    private val gson = Gson()
    @TypeConverter
    fun fromProgramDataList(programData: List<ProgramData?>?): String? {
        if (programData == null) {
            return null
        }
        val type: Type = object : TypeToken<List<ProgramData?>?>() {}.type
        return gson.toJson(programData, type)
    }

    @TypeConverter
    fun toProgramDataList(data: String?): List<ProgramData>? {
        if (data == null) {
            return null
        }
        val type: Type = object : TypeToken<List<ProgramData?>?>() {}.type
        return gson.fromJson(data, type)
    }
}

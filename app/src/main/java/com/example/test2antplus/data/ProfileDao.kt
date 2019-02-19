package com.example.test2antplus.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.test2antplus.Profile
import io.reactivex.Single

@Dao
interface ProfileDao {

    @Query("SELECT * from profile")
    fun getAll(): Single<List<Profile>>

    @Query("Select * from profile where name = :profileName")
    fun getProfile(profileName: String): Single<Profile>

    @Insert
    fun addProfile(profile: Profile)

    @Delete
    fun deleteProfile(profile: Profile)
}
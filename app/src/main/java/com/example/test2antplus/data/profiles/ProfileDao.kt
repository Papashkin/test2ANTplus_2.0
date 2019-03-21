package com.example.test2antplus.data.profiles

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface ProfileDao {

    @Query("SELECT * from profile")
    fun getAll(): Flowable<List<Profile>>

    @Query("Select * from profile where name = :profileName")
    fun getProfile(profileName: String): Single<Profile>

    @Insert
    fun addProfile(profile: Profile)

    @Delete
    fun deleteProfile(profile: Profile)
}
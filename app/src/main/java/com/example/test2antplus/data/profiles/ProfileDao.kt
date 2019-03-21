package com.example.test2antplus.data.profiles

import androidx.room.*
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

    @Update
    fun updateProfile(profile: Profile)

    @Delete
    fun deleteProfile(profile: Profile)
}
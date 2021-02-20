package com.antsfamily.biketrainer.data.models.profile

import androidx.room.Embedded
import androidx.room.Relation
import com.antsfamily.biketrainer.data.models.profile.Profile
import com.antsfamily.biketrainer.data.models.program.Program

data class ProfileWithPrograms(
    @Embedded val profile: Profile,
    @Relation(entityColumn = "username", parentColumn = "name", entity = Program::class)
    val programs: List<Program>
)

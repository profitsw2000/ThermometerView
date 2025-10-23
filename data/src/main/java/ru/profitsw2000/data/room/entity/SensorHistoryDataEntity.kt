package ru.profitsw2000.data.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(indices = [Index(value = ["date"], unique = true)])
data class SensorHistoryDataEntity(
    @PrimaryKey
    val id: ULong,
    val localId: Int,
    val sensorId: ULong,
    val letterCode: Int,
    val date: Date,
    val temperature: Double
)

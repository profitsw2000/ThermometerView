package ru.profitsw2000.data.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(indices = [Index(value = ["date", "sensorId"], unique = true)])
data class SensorHistoryDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val localId: Int,
    val sensorId: Long,
    val letterCode: Int,
    val date: Date,
    val temperature: Double
)

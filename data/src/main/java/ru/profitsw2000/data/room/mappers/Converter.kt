package ru.profitsw2000.data.room.mappers

import androidx.room.TypeConverter
import java.util.Date

class Converter {

    @TypeConverter
    fun toDate(dateLong: Long) : Date {
        return Date(dateLong)
    }
    @TypeConverter
    fun fromDate(date: Date) : Long {
        return date.time
    }
}
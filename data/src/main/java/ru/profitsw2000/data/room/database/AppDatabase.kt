package ru.profitsw2000.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.profitsw2000.data.room.dao.SensorHistoryDao
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity
import ru.profitsw2000.data.room.mappers.Converter

@Database(
    entities = [SensorHistoryDataEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract val sensorHistoryDao: SensorHistoryDao

    companion object {

        private const val DB_NAME = "database.db"
        private var instance: AppDatabase? = null

        fun getInstance() = instance ?: throw RuntimeException("Database has not been created. Please call create(context)")

        fun create(context: Context) {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                    .build()
            }
        }
    }
}
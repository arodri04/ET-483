package com.example.samrcourselab.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CourseEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListTypeConverter::class)
abstract class CourseDatabase : RoomDatabase() {
    abstract val courseDao: CourseDao

    companion object {
        const val DB_NAME = "courses.db"

        @Volatile
        private var INSTANCE: CourseDatabase? = null

        fun getDatabase(context: Context): CourseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CourseDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
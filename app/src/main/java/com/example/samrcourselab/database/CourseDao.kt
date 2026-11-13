package com.example.samrcourselab.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("SELECT * FROM CourseEntity")
    fun getCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("UPDATE CourseEntity SET isEnrolled = :isEnrolled WHERE courseCode = :courseCode")
    suspend fun updateEnrollmentState(courseCode: String, isEnrolled: Boolean)
}

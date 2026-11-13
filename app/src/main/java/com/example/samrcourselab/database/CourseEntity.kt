package com.example.samrcourselab.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CourseEntity(
    @PrimaryKey
    val courseCode: String,
    val title: String,
    val credits: Int,
    val instructor: String,
    val prerequisite: String? = null,
    val tags: List<String> = emptyList(),
    val instructorImageUrl: String,
    val isEnrolled: Boolean = false
)
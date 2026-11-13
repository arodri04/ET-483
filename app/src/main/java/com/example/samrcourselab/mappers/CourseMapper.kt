package com.example.samrcourselab.mappers

import com.example.samrcourselab.CourseProfile
import com.example.samrcourselab.database.CourseEntity

fun CourseProfile.toEntity(isEnrolled: Boolean = false): CourseEntity {
    return CourseEntity(
        courseCode = this.courseCode,
        title = this.title,
        credits = this.credits,
        instructor = this.instructor,
        prerequisite = this.prerequisite,
        tags = this.tags,
        instructorImageUrl = this.instructorImageUrl,
        isEnrolled = isEnrolled
    )
}

fun CourseEntity.toDomain(): CourseProfile {
    return CourseProfile(
        courseCode = this.courseCode,
        title = this.title,
        credits = this.credits,
        instructor = this.instructor,
        prerequisite = this.prerequisite,
        tags = this.tags,
        instructorImageUrl = this.instructorImageUrl
    )
}
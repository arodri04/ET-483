package com.example.samrcourselab

data class CourseUiState(
    val searchQuery: String = "",
    val overridePrereqs: Boolean = false,
    val enrolledCourseIds: Set<String> = emptySet(),
    val satisfiedPrereqIds: Set<String> = emptySet(),
    val selectedCourseId: String? = null
)


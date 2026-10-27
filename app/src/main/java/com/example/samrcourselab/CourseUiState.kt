package com.example.samrcourselab

data class CourseUiState(
    val courses: List<CourseProfile> = emptyList(),
    val rawNetworkCourses: List<CourseDto> = emptyList(),
    val searchQuery: String = "",
    val overridePrereqs: Boolean = false,
    val enrolledCourseIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCourseId: String? = null
)


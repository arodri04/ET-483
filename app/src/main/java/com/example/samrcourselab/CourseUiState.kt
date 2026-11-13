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


) {
    fun isEnrolled(courseCode: String): Boolean = courseCode in enrolledCourseIds
    fun canEnroll(prerequisite: String?): Boolean = prerequisite == null || overridePrereqs
    val selectedCourse: CourseProfile? get() = courses.find { it.courseCode == selectedCourseId }
}


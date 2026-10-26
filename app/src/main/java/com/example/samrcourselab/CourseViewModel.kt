package com.example.samrcourselab

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CourseViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update { current ->
            current.copy(searchQuery = query)
        }
    }

    fun toggleOverridePrerequisites() {
        _uiState.update { current ->
            current.copy(overridePrereqs = !current.overridePrereqs)
        }
    }

    fun toggleEnrollment(courseCode: String) {
        _uiState.update { current ->
            val isCurrentlyEnrolled = courseCode in current.enrolledCourseIds

            val updatedSet = if (isCurrentlyEnrolled) {
                current.enrolledCourseIds - courseCode
            } else {
                current.enrolledCourseIds + courseCode
            }

            current.copy(enrolledCourseIds = updatedSet)
        }
    }

}
package com.example.samrcourselab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException


class CourseViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    init {
        fetchCoursesFromApi()
    }

    fun fetchCoursesFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { current ->
                current.copy(isLoading = true, errorMessage = null)
            }

            try {
                println("--- Checking API ---")
                val networkCourses = ApiClient.courseApiService.fetchCourses()

                _uiState.update { current ->
                    current.copy(
                        rawNetworkCourses = networkCourses,
                        errorMessage = null
                    )
                }
                println("--- API connected ---")

            } catch (e: SocketTimeoutException) {
                _uiState.update { current ->
                    current.copy(errorMessage = "Server connection timed out. Please check if your server is running on host 0.0.0.0.")
                }
            } catch (e: IOException) {
                _uiState.update { current ->
                    current.copy(errorMessage = "Network error: Unable to reach the server. Check your connection or cleartext configuration.")
                }
            } catch (e: Exception) {
                _uiState.update { current ->
                    current.copy(errorMessage = "An unexpected error occurred while parsing course profiles.")
                }
                e.printStackTrace()
            } finally {
                _uiState.update { current ->
                    current.copy(isLoading = false)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { current -> current.copy(searchQuery = query) }
    }

    fun toggleOverridePrerequisites() {
        _uiState.update { current -> current.copy(overridePrereqs = !current.overridePrereqs) }
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
package com.example.samrcourselab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.samrcourselab.data.UserPreferencesRepository
import com.example.samrcourselab.database.CourseDao
import com.example.samrcourselab.mappers.toDomain
import com.example.samrcourselab.mappers.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

class CourseViewModel(
    private val courseDao: CourseDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CourseUiState())
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    init {
        observeDatabaseCourses()
        observeUserPreferences()
        fetchCoursesFromApi()
    }

    private fun observeDatabaseCourses() {
        viewModelScope.launch {
            courseDao.getCourses().collect { entities ->
                val domainProfiles = entities.map { it.toDomain() }
                val enrolledIds = entities
                    .filter { it.isEnrolled }
                    .map { it.courseCode }
                    .toSet()

                _uiState.update { current ->
                    current.copy(
                        courses = domainProfiles,
                        enrolledCourseIds = enrolledIds
                    )
                }
            }
        }
    }

    fun fetchCoursesFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { current ->
                current.copy(isLoading = true, errorMessage = null)
            }

            try {
                println("--- Checking API ---")
                val networkCourses = ApiClient.courseApiService.fetchCourses()

                val existingEnrolledIds = courseDao.getCourses()
                    .first()
                    .filter { it.isEnrolled }
                    .map { it.courseCode }
                    .toSet()

                val entitiesToInsert = networkCourses.map { dto ->
                    val domain = dto.toDomain()
                    domain.toEntity(isEnrolled = domain.courseCode in existingEnrolledIds)
                }

                courseDao.insertCourses(entitiesToInsert)

                _uiState.update { current ->
                    current.copy(
                        rawNetworkCourses = networkCourses,
                        errorMessage = null
                    )
                }
                println("--- API connected and cached to Room ---")

            } catch (e: SocketTimeoutException) {
                _uiState.update { current ->
                    current.copy(errorMessage = "Server connection timed out.")
                }
            } catch (e: IOException) {
                _uiState.update { current ->
                    current.copy(errorMessage = "Network error: Unable to reach the server.")
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

    fun updateSelectedCourseId(courseId: String?) {
        _uiState.update { current ->
            current.copy(selectedCourseId = courseId)
        }
    }

    fun toggleEnrollment(courseCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isCurrentlyEnrolled = courseCode in _uiState.value.enrolledCourseIds
            courseDao.updateEnrollmentState(
                courseCode = courseCode,
                isEnrolled = !isCurrentlyEnrolled
            )
        }
    }

    private fun observeUserPreferences() {
        // Observe Prerequisite Override switch from DataStore
        viewModelScope.launch {
            userPreferencesRepository.overridePrereqsFlow.collect { isOverridden ->
                _uiState.update { it.copy(overridePrereqs = isOverridden) }
            }
        }

        // Observe Search Query string from DataStore
        viewModelScope.launch {
            userPreferencesRepository.searchQueryFlow.collect { savedQuery ->
                _uiState.update { it.copy(searchQuery = savedQuery) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        // Update UI immediately for smooth typing, then persist to DataStore
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.updateSearchQuery(query)
        }
    }

    fun toggleOverridePrerequisites() {
        val newValue = !_uiState.value.overridePrereqs
        viewModelScope.launch(Dispatchers.IO) {
            userPreferencesRepository.updateOverridePrereqs(newValue)
        }
    }

}


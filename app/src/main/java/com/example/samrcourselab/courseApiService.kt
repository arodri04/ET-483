package com.example.samrcourselab

import retrofit2.http.GET
import com.google.gson.annotations.SerializedName



interface CourseApiService {
    @GET("api/courses")
    suspend fun fetchCourses(): List<CourseDto>
}
data class CourseDto(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("credits") val credits: Int,
    @SerializedName("instructor") val instructor: String?,
    @SerializedName("instructorImageUrl") val instructorImageUrl: String?,
    @SerializedName("prerequisite") val prerequisite: String?,
    @SerializedName("tags") val tags: List<String>? = emptyList()
)


fun CourseDto.toDomain(): CourseProfile {
    return CourseProfile(
        courseCode = this.id.orEmpty(),
        title = this.title.orEmpty(),
        credits = this.credits ?: 0,
        instructor = this.instructor.orEmpty(),
        instructorImageUrl = this.instructorImageUrl.orEmpty(),
        prerequisite = this.prerequisite.orEmpty(),
        tags = this.tags ?: emptyList()
    )
}
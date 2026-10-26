package com.example.samrcourselab

data class CourseProfile(
    val courseCode: String,
    val title: String,
    val credits: Int,
    val instructor: String,
    val prerequisite: String?,
    val tags: List<String>
)

object CourseRepo {
    fun SampleOne(): CourseProfile {
        return CourseProfile(
            courseCode = "E T 483",
            title = "Mobile App Programming",
            credits = 4,
            instructor = "Masadeh",
            prerequisite = null,
            tags = listOf("Kotlin", "Compose", "Android")
        )
    }

    fun formatTags(tags: List<String>, formatter: (String) -> String):
            List<String> {
        return tags.map { formatter(it)}
    }
}
package com.example.samrcourselab

data class CourseProfile(
    val courseCode: String,
    val title: String,
    val credits: Int,
    val instructor: String,
    val prerequisite: String? = null,
    val tags: List<String> = emptyList(),
    val instructorImageUrl: String
)

fun formatTags(tags: List<String>, formatter: (String) -> String):
        List<String> {
    return tags.map { formatter(it)}
}

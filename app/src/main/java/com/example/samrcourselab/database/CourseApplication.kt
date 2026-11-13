package com.example.samrcourselab.database

import android.app.Application


class CourseApplication : Application() {
    val database: CourseDatabase by lazy {
        CourseDatabase.getDatabase(this)
    }
}
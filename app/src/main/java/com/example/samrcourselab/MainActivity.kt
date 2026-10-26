package com.example.samrcourselab


import android.R.attr.type
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavType
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("Starting")
        setContent {
            val navController = rememberNavController()

            val courseList: Array<CourseProfile> = arrayOf(
                CourseProfile(
                    "E T 483",
                    "Mobile App Programming",
                    4,
                    "Masadeh",
                    null,
                    listOf("Kotlin", "Compose", "Android"),
                    instructorImageUrl = ""
                ),
                CourseProfile(
                    "ICT 458",
                    "Web Dev and DB Applications",
                    4,
                    "Ball",
                    "Intro To Programming",
                    listOf("SQL", "noSQL", "Web"),
                    instructorImageUrl = ""
                ),
                CourseProfile(
                    "ICT 380",
                    "Python",
                    4,
                    "Braker",
                    null,
                    listOf("Flask", "Django", "Automation"),
                    instructorImageUrl = "https://et.nmsu.edu/images/Braker.jpg"
                ),
                CourseProfile(
                    "ICT 145",
                    "Network Essentials",
                    4,
                    "Ball",
                    "IT Essentials",
                    listOf("Flask", "Django", "Automation"),
                    instructorImageUrl = ""
                ),
                CourseProfile(
                    "ICT 220",
                    "Discreet Math",
                    4,
                    "Braker",
                    null,
                    listOf("Flask", "Django", "Automation"),
                    instructorImageUrl = "https://et.nmsu.edu/images/Braker.jpg"
                )
            )


            val viewModel = androidx.lifecycle.ViewModelProvider(this)[CourseViewModel::class.java]
            val uiState by viewModel.uiState.collectAsState()

            val onOverrideToggle = {
                viewModel.toggleOverridePrerequisites()
            }

            val filteredCourses = courseList.filter { course ->
                val query = uiState.searchQuery.trim()
                query.isEmpty() ||
                        course.title.contains(query, ignoreCase = true) ||
                        course.courseCode.contains(query, ignoreCase = true) ||
                        course.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }

            val totalCredits = courseList
                .filter { it.courseCode in uiState.enrolledCourseIds }
                .sumOf { it.credits }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "course_list"
                    ) {
                        composable("course_list") {
                            CourseScreen(
                                courses = courseList,
                                filteredCourses = filteredCourses,
                                onSearchQueryChange = { newQuery ->
                                    viewModel.updateSearchQuery(newQuery)
                                },
                                onOverrideToggle = onOverrideToggle,
                                viewModel = viewModel,
                                totalCredits = totalCredits,
                                uiState = uiState,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onCourseClick = { courseId ->
                                    navController.navigate("course_detail/$courseId")
                                }

                            )
                        }

                        composable(
                            route = "course_detail/{courseId}",
                            arguments = listOf(navArgument("courseId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val courseId = backStackEntry.arguments?.getString("courseId")

                            CourseDetailScreen(
                                courseId = courseId,
                                courses = courseList,
                                filteredCourses = filteredCourses,
                                onSearchQueryChange = { newQuery ->
                                    viewModel.updateSearchQuery(
                                        newQuery
                                    )
                                },
                                onOverrideToggle = onOverrideToggle,
                                totalCredits = totalCredits,
                                viewModel = viewModel,
                                uiState = uiState,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    courses: Array<CourseProfile>,
    filteredCourses: List<CourseProfile>,
    onSearchQueryChange: (String) ->Unit,
    onOverrideToggle: () -> Unit,
    totalCredits: Int,
    viewModel: CourseViewModel,
    uiState: CourseUiState,
    onCourseClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier.background(color = Color.DarkGray)

) {
    Scaffold(
        topBar = {
            CustomTopBar(
                totalCredits = totalCredits,
                overridePrereqs = uiState.overridePrereqs,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onOverrideToggle = onOverrideToggle,
                isDetailView = false,
                courseId = null,
                onBackClick = onBackClick,
                modifier = Modifier.wrapContentHeight()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(
                modifier = Modifier
                    .padding(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (filteredCourses.isEmpty()) {
                    items(courses) { course ->
                        val canEnroll = course.prerequisite == null || uiState.overridePrereqs
                        val isEnrolled = course.courseCode in uiState.enrolledCourseIds

                        CourseCard(
                            course = course,
                            isEnrolled = isEnrolled,
                            canEnroll = canEnroll,
                            onEnrollToggle = {viewModel.toggleEnrollment(course.courseCode)},
                            modifier = Modifier.clickable {
                                onCourseClick(course.courseCode)
                            }
                        )
                    }

                } else {
                    items(filteredCourses) { course ->
                        val canEnroll = course.prerequisite == null || uiState.overridePrereqs
                        val isEnrolled = course.courseCode in uiState.enrolledCourseIds

                        CourseCard(
                            course = course,
                            isEnrolled = isEnrolled,
                            canEnroll = canEnroll,
                            modifier = Modifier.clickable {
                                onCourseClick(course.courseCode)
                            },
                            onEnrollToggle = {viewModel.toggleEnrollment(course.courseCode)}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CourseDetailScreen(
    courseId: String?,
    courses: Array<CourseProfile>,
    filteredCourses: List<CourseProfile>,
    onSearchQueryChange: (String) -> Unit,
    onOverrideToggle: () -> Unit,
    totalCredits: Int,
    viewModel: CourseViewModel,
    uiState: CourseUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier.background(color = Color.DarkGray),
) {
    val selectedCourse = courses.find { it.courseCode == courseId }

    Scaffold(
        topBar = {
            CustomTopBar(
                totalCredits = totalCredits,
                overridePrereqs = uiState.overridePrereqs,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onOverrideToggle = onOverrideToggle,
                isDetailView = true,
                onBackClick = onBackClick,
                courseId = courseId,
                modifier = Modifier.wrapContentHeight()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            LazyColumn(
                modifier = Modifier.padding(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedCourse == null) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Course details unavailable for ID: $courseId",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    val isEnrolled = selectedCourse.courseCode in uiState.enrolledCourseIds
                    val canEnroll = selectedCourse.prerequisite == null || uiState.overridePrereqs

                    item {
                        Card(modifier = Modifier.fillMaxWidth()
                                            .then(
                                        if (isEnrolled) {
                                                    Modifier.border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                                                } else {
                                                    Modifier
                                                }
                                            ),
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = selectedCourse.courseCode)
                                        Text(text = "${selectedCourse.credits} Credits")
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp, horizontal = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedCourse.title,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }


                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        AsyncImage(
                                            model = selectedCourse.instructorImageUrl,
                                            contentDescription = "Instructor Avatar Picture",
                                            placeholder = painterResource(id = R.drawable.baseline_person_24),
                                            error = painterResource(id = R.drawable.baseline_person_24),
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                        )

                                        Column {
                                            DetailRow(label = "Instructor:", value = selectedCourse.instructor)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        selectedCourse.tags.forEach { tag ->
                                            AssistChip(
                                                onClick = {},
                                                label = { Text(text = tag) },
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (selectedCourse.prerequisite != null) {
                                        DetailRow(label = "Prerequisite:", value = selectedCourse.prerequisite)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = { viewModel.toggleEnrollment(selectedCourse.courseCode) },
                                        enabled = canEnroll || isEnrolled,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isEnrolled) MaterialTheme.colorScheme.error
                                            else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            text = if (isEnrolled) "Drop Course" else "Enroll",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}


@Composable
fun CourseCard(
    course: CourseProfile,
    isEnrolled: Boolean,
    canEnroll: Boolean,
    onEnrollToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
            .then(
                if (isEnrolled) {
                    Modifier.border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),

    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),


        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = course.courseCode)
                Text(text = "${course.credits} Credits")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    text = course.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = course.instructorImageUrl,
                contentDescription = "Instructor Avatar Picture",
                placeholder = painterResource(id = R.drawable.baseline_person_24),
                error = painterResource(id = R.drawable.baseline_person_24),
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isEnrolled) {
                OutlinedButton(
                    onClick = onEnrollToggle,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Drop Course")
                }
            } else {
                Button(
                    onClick = onEnrollToggle,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canEnroll,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (canEnroll) "Enroll" else "Locked")
                }


                if (!canEnroll) {
                    Text(
                        text = "Requires prerequisite course: ${course.prerequisite}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, color =
            Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CustomTopBar(
    totalCredits: Int,
    courseId: String?,
    overridePrereqs: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onOverrideToggle: () -> Unit,
    isDetailView: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isDetailView) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Courses",
                    style = MaterialTheme.typography.titleLarge
                )
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search courses...") },
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Credits: $totalCredits",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(
                    onClick = onOverrideToggle
                ) {
                    Text(
                        text = if (overridePrereqs) "Lock Prereqs" else "Skip Prereqs"
                    )
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = "$courseId",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Credits: $totalCredits",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
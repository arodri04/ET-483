package com.example.courseapi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class controller {
    
    @RequestMapping("api/courses")
    public List<courses> getCourses() {
        List<courses> courseList = new ArrayList<>();

		courseList.add(new courses("ICT 480","Python",4,"Braker",null, List.of("flask", "django"),""));
        courseList.add(new courses("ICT 101", "Introduction to Java", 3, "Smith", null, List.of("basics", "oop", "syntax"), ""));
        courseList.add(new courses("ICT 320", "Web Development", 4, "Davis", "ICT 101", List.of("html", "css", "javascript", "react"), ""));
        courseList.add(new courses("ICT 495", "Cloud Architecture", 3, "Johnson", "ICT 480", List.of("aws", "devops", "docker"), ""));
        return courseList;  
    } 
      
}

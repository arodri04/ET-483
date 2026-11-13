package com.example.courseapi;

import java.util.List;

public class courses {
    private String id;
    private String title;
    private int credits;
    private String instructor;
    private String prerequisite;
    private List<String> tags;
    private String instructorImageUrl;

    public courses(String id, String title, int credits, String instructor, String prerequisite, List<String> tags, String instructorImageUrl) {
        this.id = id;
        this.title = title;
        this.credits = credits;
        this.instructor = instructor;
        this.prerequisite = prerequisite;
        this.tags = tags;
        this.instructorImageUrl = instructorImageUrl;
    }
    
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getPrerequisite() { return prerequisite; }
    public void setPrerequisite(String prerequisite) { this.prerequisite = prerequisite; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getInstructorImageUrl() { return instructorImageUrl; }
    public void setInstructorImageUrl(String instructorImageUrl) { this.instructorImageUrl = instructorImageUrl; }
}

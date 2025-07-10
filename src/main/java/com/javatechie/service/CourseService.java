package com.javatechie.service;

import com.javatechie.dto.Course;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CourseService {
    
    private final List<Course> courses = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public CourseService() {
        // Initialize with some sample data
        courses.add(new Course(1L, "Java Basics", 99.99));
        courses.add(new Course(2L, "Spring Boot", 149.99));
        courses.add(new Course(3L, "Azure Functions", 199.99));
    }
    
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }
    
    public Course getCourseById(Long id) {
        return courses.stream()
                .filter(course -> course.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public Course createCourse(Course course) {
        course.setId(idGenerator.getAndIncrement() + 3);
        courses.add(course);
        return course;
    }
    
    public Course updateCourse(Long id, Course updatedCourse) {
        Course existingCourse = getCourseById(id);
        if (existingCourse != null) {
            existingCourse.setName(updatedCourse.getName());
            existingCourse.setPrice(updatedCourse.getPrice());
            return existingCourse;
        }
        return null;
    }
    
    public boolean deleteCourse(Long id) {
        return courses.removeIf(course -> course.getId().equals(id));
    }
}

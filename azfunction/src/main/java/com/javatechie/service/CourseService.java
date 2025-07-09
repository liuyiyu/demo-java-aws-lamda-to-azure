package com.javatechie.service;

import com.javatechie.dto.Course;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CourseService {

    private final List<Course> courses = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong();

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
        course.setId(idGenerator.incrementAndGet());
        courses.add(course);
        return course;
    }

    public Course updateCourse(Long id, Course course) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(id)) {
                course.setId(id);
                courses.set(i, course);
                return course;
            }
        }
        return null;
    }

    public boolean deleteCourse(Long id) {
        return courses.removeIf(course -> course.getId().equals(id));
    }
}

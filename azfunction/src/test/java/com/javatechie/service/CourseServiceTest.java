package com.javatechie.service;

import com.javatechie.dto.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CourseServiceTest {

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseService();
    }

    @Test
    void addCourse_ShouldAddCourseSuccessfully() {
        // Arrange
        Course course = new Course(1, "Java Programming", 99.99);

        // Act
        Course result = courseService.addCourse(course);

        // Assert
        assertNotNull(result);
        assertEquals(course.getId(), result.getId());
        assertEquals(course.getName(), result.getName());
        assertEquals(course.getPrice(), result.getPrice());
    }

    @Test
    void getAllCourses_ShouldReturnEmptyList_WhenNoCourses() {
        // Act
        List<Course> courses = courseService.getAllCourses();

        // Assert
        assertNotNull(courses);
        assertTrue(courses.isEmpty());
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        // Arrange
        Course course1 = new Course(1, "Java Programming", 99.99);
        Course course2 = new Course(2, "Python Programming", 89.99);
        courseService.addCourse(course1);
        courseService.addCourse(course2);

        // Act
        List<Course> courses = courseService.getAllCourses();

        // Assert
        assertNotNull(courses);
        assertEquals(2, courses.size());
        assertTrue(courses.contains(course1));
        assertTrue(courses.contains(course2));
    }

    @Test
    void getCourseById_ShouldReturnCourse_WhenExists() {
        // Arrange
        Course course = new Course(1, "Java Programming", 99.99);
        courseService.addCourse(course);

        // Act
        Optional<Course> result = courseService.getCourseById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(course.getId(), result.get().getId());
        assertEquals(course.getName(), result.get().getName());
        assertEquals(course.getPrice(), result.get().getPrice());
    }

    @Test
    void getCourseById_ShouldReturnEmpty_WhenNotExists() {
        // Act
        Optional<Course> result = courseService.getCourseById(1);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void updateCourse_ShouldUpdateSuccessfully_WhenExists() {
        // Arrange
        Course originalCourse = new Course(1, "Java Programming", 99.99);
        courseService.addCourse(originalCourse);
        Course updatedCourse = new Course(1, "Updated Java Programming", 149.99);

        // Act
        Optional<Course> result = courseService.updateCourse(1, updatedCourse);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(updatedCourse.getName(), result.get().getName());
        assertEquals(updatedCourse.getPrice(), result.get().getPrice());
    }

    @Test
    void updateCourse_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        Course updatedCourse = new Course(1, "Updated Java Programming", 149.99);

        // Act
        Optional<Course> result = courseService.updateCourse(1, updatedCourse);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deleteCourse_ShouldDeleteSuccessfully_WhenExists() {
        // Arrange
        Course course = new Course(1, "Java Programming", 99.99);
        courseService.addCourse(course);

        // Act
        boolean result = courseService.deleteCourse(1);

        // Assert
        assertTrue(result);
        assertTrue(courseService.getAllCourses().isEmpty());
    }

    @Test
    void deleteCourse_ShouldReturnFalse_WhenNotExists() {
        // Act
        boolean result = courseService.deleteCourse(1);

        // Assert
        assertFalse(result);
    }
}

package com.javatechie.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.javatechie.dto.Course;
import com.javatechie.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Component
public class CourseFunction {
    
    @Autowired
    private CourseService courseService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FunctionName("getAllCourses")
    public HttpResponseMessage getAllCourses(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS,
                route = "courses"
            ) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        
        context.getLogger().info("Getting all courses");
        try {
            List<Course> courses = courseService.getAllCourses();
            return request
                    .createResponseBuilder(HttpStatus.OK)
                    .body(courses)
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error getting all courses: " + e.getMessage());
            return request
                    .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving courses: " + e.getMessage())
                    .build();
        }
    }

    @FunctionName("getCourseById")
    public HttpResponseMessage getCourseById(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS,
                route = "courses/{id}"
            ) HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        
        context.getLogger().info("Getting course by ID: " + id);
        
        try {
            Course course = courseService.getCourseById(Long.parseLong(id));
            if (course != null) {
                return request
                        .createResponseBuilder(HttpStatus.OK)
                        .body(course)
                        .build();
            } else {
                return request
                        .createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Course not found with ID: " + id)
                        .build();
            }
        } catch (NumberFormatException e) {
            context.getLogger().warning("Invalid course ID format: " + id);
            return request
                    .createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid course ID format: " + id)
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error getting course by ID: " + e.getMessage());
            return request
                    .createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving course: " + e.getMessage())
                    .build();
        }
    }

    @FunctionName("createCourse")
    public HttpResponseMessage createCourse(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS,
                route = "courses"
            ) HttpRequestMessage<String> request,
            final ExecutionContext context) {
        
        context.getLogger().info("Creating new course");
        
        try {
            String requestBody = request.getBody();
            Course course = objectMapper.readValue(requestBody, Course.class);
            
            // Validate course
            if (course.getName() == null || course.getName().trim().isEmpty()) {
                return request
                        .createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Course name is required")
                        .build();
            }
            
            if (course.getPrice() <= 0) {
                return request
                        .createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Price must be greater than 0")
                        .build();
            }
            
            Course savedCourse = courseService.createCourse(course);
            return request
                    .createResponseBuilder(HttpStatus.CREATED)
                    .body(savedCourse)
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error creating course: " + e.getMessage());
            return request
                    .createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Error creating course: " + e.getMessage())
                    .build();
        }
    }
}

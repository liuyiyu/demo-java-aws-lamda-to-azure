package com.javatechie.function;

import com.javatechie.dto.Course;
import com.javatechie.service.CourseService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public class CourseFunction {

    private final CourseService courseService = new CourseService();
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
        
        context.getLogger().info("Processing get all courses request.");
        List<Course> courses = courseService.getAllCourses();
        
        return request.createResponseBuilder(HttpStatus.OK)
                .body(courses)
                .header("Content-Type", "application/json")
                .build();
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
        
        context.getLogger().info("Processing get course by id request for id: " + id);
        try {
            Course course = courseService.getCourseById(Long.parseLong(id));
            
            if (course != null) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .body(course)
                        .header("Content-Type", "application/json")
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Course not found with id: " + id)
                        .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid course ID format: " + id)
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
        
        try {
            String requestBody = request.getBody();
            Course course = objectMapper.readValue(requestBody, Course.class);
            
            if (course == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Course data is required")
                        .build();
            }

            context.getLogger().info("Processing create course request.");
            Course createdCourse = courseService.createCourse(course);
            
            return request.createResponseBuilder(HttpStatus.CREATED)
                    .body(createdCourse)
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Error creating course: " + e.getMessage())
                    .build();
        }
    }

    @FunctionName("updateCourse")
    public HttpResponseMessage updateCourse(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "courses/{id}"
            ) HttpRequestMessage<String> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        
        try {
            String requestBody = request.getBody();
            Course course = objectMapper.readValue(requestBody, Course.class);
            
            if (course == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Course data is required")
                        .build();
            }

            context.getLogger().info("Processing update course request for id: " + id);
            Course updatedCourse = courseService.updateCourse(Long.parseLong(id), course);
            
            if (updatedCourse != null) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .body(updatedCourse)
                        .header("Content-Type", "application/json")
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Course not found with id: " + id)
                        .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid course ID format: " + id)
                    .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Error updating course: " + e.getMessage())
                    .build();
        }
    }

    @FunctionName("deleteCourse")
    public HttpResponseMessage deleteCourse(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "courses/{id}"
            ) HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        
        context.getLogger().info("Processing delete course request for id: " + id);
        try {
            boolean deleted = courseService.deleteCourse(Long.parseLong(id));
            
            if (deleted) {
                return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Course not found with id: " + id)
                        .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid course ID format: " + id)
                    .build();
        }
    }

    @FunctionName("ping")
    public HttpResponseMessage ping(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS,
                route = "ping"
            ) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        
        context.getLogger().info("Processing ping request.");
        
        return request.createResponseBuilder(HttpStatus.OK)
                .body("Application is running!")
                .header("Content-Type", "text/plain")
                .build();
    }
}

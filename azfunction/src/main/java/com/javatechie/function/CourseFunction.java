package com.javatechie.function;

import com.javatechie.dto.Course;
import com.javatechie.service.CourseService;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CourseFunction {

    @Autowired
    private CourseService courseService;

    @FunctionName("GetAllCourses")
    public HttpResponseMessage getAllCourses(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "courses"
            ) HttpRequestMessage<Void> request,
            final ExecutionContext context) {
        
        context.getLogger().info("Processing get all courses request.");
        List<Course> courses = courseService.getAllCourses();
        
        return request.createResponseBuilder(HttpStatus.OK)
                .body(courses)
                .header("Content-Type", "application/json")
                .build();
    }

    @FunctionName("GetCourseById")
    public HttpResponseMessage getCourseById(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "courses/{id}"
            ) HttpRequestMessage<Void> request,
            @BindingName("id") int id,
            final ExecutionContext context) {
        
        context.getLogger().info("Processing get course by id request for id: " + id);
        Optional<Course> course = courseService.getCourseById(id);
        
        if (course.isPresent()) {
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(course.get())
                    .header("Content-Type", "application/json")
                    .build();
        } else {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Course not found with id: " + id)
                    .build();
        }
    }

    @FunctionName("CreateCourse")
    public HttpResponseMessage createCourse(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "courses"
            ) HttpRequestMessage<Course> request,
            final ExecutionContext context) {
        
        Course course = request.getBody();
        if (course == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Course data is required")
                    .build();
        }

        context.getLogger().info("Processing create course request.");
        Course createdCourse = courseService.addCourse(course);
        
        return request.createResponseBuilder(HttpStatus.CREATED)
                .body(createdCourse)
                .header("Content-Type", "application/json")
                .build();
    }

    @FunctionName("UpdateCourse")
    public HttpResponseMessage updateCourse(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "courses/{id}"
            ) HttpRequestMessage<Course> request,
            @BindingName("id") int id,
            final ExecutionContext context) {
        
        Course course = request.getBody();
        if (course == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Course data is required")
                    .build();
        }

        context.getLogger().info("Processing update course request for id: " + id);
        Optional<Course> updatedCourse = courseService.updateCourse(id, course);
        
        if (updatedCourse.isPresent()) {
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(updatedCourse.get())
                    .header("Content-Type", "application/json")
                    .build();
        } else {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Course not found with id: " + id)
                    .build();
        }
    }

    @FunctionName("DeleteCourse")
    public HttpResponseMessage deleteCourse(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "courses/{id}"
            ) HttpRequestMessage<Void> request,
            @BindingName("id") int id,
            final ExecutionContext context) {
        
        context.getLogger().info("Processing delete course request for id: " + id);
        boolean deleted = courseService.deleteCourse(id);
        
        if (deleted) {
            return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                    .build();
        } else {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Course not found with id: " + id)
                    .build();
        }
    }
}

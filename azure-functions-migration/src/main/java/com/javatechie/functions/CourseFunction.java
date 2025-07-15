package com.javatechie.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.javatechie.dto.Course;
import com.javatechie.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import java.util.List;
import java.util.Optional;

public class CourseFunction extends FunctionInvoker<String, Object> {

    @Autowired
    private CourseService courseService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FunctionName("getAllCourses")
    public HttpResponseMessage getAllCourses(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.GET},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            List<Course> courses = courseService.getAllCourses();
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(courses)
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error getting courses: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving courses")
                    .build();
        }
    }

    @FunctionName("createCourse")
    public HttpResponseMessage createCourse(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.POST},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String requestBody = request.getBody().orElse("{}");
            Course course = objectMapper.readValue(requestBody, Course.class);
            courseService.addCourse(course);
            
            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(course)
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error creating course: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Error creating course")
                    .build();
        }
    }

    @FunctionName("getCourseById")
    public HttpResponseMessage getCourseById(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.GET},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses/{id}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String idParam = request.getQueryParameters().get("id");
            if (idParam == null) {
                // Try to get from path
                String path = request.getUri().getPath();
                String[] pathParts = path.split("/");
                idParam = pathParts[pathParts.length - 1];
            }
            
            int id = Integer.parseInt(idParam);
            Optional<Course> course = courseService.getCourseById(id);
            
            if (course.isPresent()) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(course.get())
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Course not found")
                        .build();
            }
        } catch (Exception e) {
            context.getLogger().severe("Error getting course: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid course ID")
                    .build();
        }
    }

    @FunctionName("updateCourse")
    public HttpResponseMessage updateCourse(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.PUT},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses/{id}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String idParam = request.getQueryParameters().get("id");
            if (idParam == null) {
                String path = request.getUri().getPath();
                String[] pathParts = path.split("/");
                idParam = pathParts[pathParts.length - 1];
            }

            int id = Integer.parseInt(idParam);
            String requestBody = request.getBody().orElse("{}");
            Course newCourse = objectMapper.readValue(requestBody, Course.class);
            
            boolean updated = courseService.updateCourse(id, newCourse);
            if (updated) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(newCourse)
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Course not found")
                        .build();
            }
        } catch (Exception e) {
            context.getLogger().severe("Error updating course: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Error updating course")
                    .build();
        }
    }

    @FunctionName("deleteCourse")
    public HttpResponseMessage deleteCourse(
            @HttpTrigger(name = "req",
                        methods = {HttpMethod.DELETE},
                        authLevel = AuthorizationLevel.FUNCTION,
                        route = "courses/{id}")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String idParam = request.getQueryParameters().get("id");
            if (idParam == null) {
                String path = request.getUri().getPath();
                String[] pathParts = path.split("/");
                idParam = pathParts[pathParts.length - 1];
            }

            int id = Integer.parseInt(idParam);
            boolean deleted = courseService.deleteCourse(id);
            
            if (deleted) {
                return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Course not found")
                        .build();
            }
        } catch (Exception e) {
            context.getLogger().severe("Error deleting course: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Error deleting course")
                    .build();
        }
    }
}

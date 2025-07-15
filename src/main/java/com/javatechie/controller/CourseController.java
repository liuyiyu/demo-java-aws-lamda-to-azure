package com.javatechie.controller;

import com.javatechie.dto.Course;
import com.javatechie.service.CourseService;
import com.javatechie.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private S3Service s3Service;


    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        courseService.addCourse(course);
        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Course> getCourseById(@PathVariable int id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Course> updateCourse(@PathVariable int id, @RequestBody Course newCourse) {
        boolean updated = courseService.updateCourse(id, newCourse);
        if (updated) {
            return new ResponseEntity<>(newCourse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        boolean deleted = courseService.deleteCourse(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // AWS S3 Read Operations

    /**
     * Read content of a specific S3 object
     */
    @GetMapping(value = "/s3/read/{objectKey}", produces = "application/json")
    public ResponseEntity<?> readS3Object(@PathVariable String objectKey) {
        try {
            if (!s3Service.doesObjectExist(objectKey)) {
                return new ResponseEntity<>(
                    Map.of("error", "Object not found in S3: " + objectKey), 
                    HttpStatus.NOT_FOUND
                );
            }
            
            String content = s3Service.readObjectFromS3(objectKey);
            return new ResponseEntity<>(
                Map.of("objectKey", objectKey, "content", content), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                Map.of("error", "Failed to read S3 object: " + e.getMessage()), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * List all objects in the S3 bucket
     */
    @GetMapping(value = "/s3/list", produces = "application/json")
    public ResponseEntity<?> listS3Objects() {
        try {
            List<String> objects = s3Service.listObjectsInBucket();
            return new ResponseEntity<>(
                Map.of("bucket", "configured-bucket", "objects", objects), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                Map.of("error", "Failed to list S3 objects: " + e.getMessage()), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Check if an object exists in S3
     */
    @GetMapping(value = "/s3/exists/{objectKey}", produces = "application/json")
    public ResponseEntity<?> checkS3ObjectExists(@PathVariable String objectKey) {
        try {
            boolean exists = s3Service.doesObjectExist(objectKey);
            return new ResponseEntity<>(
                Map.of("objectKey", objectKey, "exists", exists), 
                HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                Map.of("error", "Failed to check S3 object existence: " + e.getMessage()), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}

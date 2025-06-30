package com.javatechie.integration;

import com.javatechie.CourseManagementApplication;
import com.javatechie.dto.Course;
import com.microsoft.azure.functions.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CourseManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CourseIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:7071/api/courses";

    @Test
    void testCourseLifecycle() {
        // Create a course
        Course newCourse = new Course(1, "Integration Test Course", 99.99);
        ResponseEntity<Course> createResponse = restTemplate.postForEntity(baseUrl, newCourse, Course.class);
        assertEquals(HttpStatus.CREATED.value(), createResponse.getStatusCodeValue());
        assertNotNull(createResponse.getBody());

        // Get the created course
        Course createdCourse = createResponse.getBody();
        ResponseEntity<Course> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + createdCourse.getId(), Course.class);
        assertEquals(HttpStatus.OK.value(), getResponse.getStatusCodeValue());
        assertEquals(createdCourse.getName(), getResponse.getBody().getName());

        // Update the course
        createdCourse.setName("Updated Course");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Course> requestEntity = new HttpEntity<>(createdCourse, headers);
        
        ResponseEntity<Course> updateResponse = restTemplate.exchange(
                baseUrl + "/" + createdCourse.getId(),
                HttpMethod.PUT,
                requestEntity,
                Course.class);
        assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCodeValue());
        assertEquals("Updated Course", updateResponse.getBody().getName());

        // Delete the course
        restTemplate.delete(baseUrl + "/" + createdCourse.getId());
        
        // Verify deletion
        ResponseEntity<Course> verifyResponse = restTemplate.getForEntity(
                baseUrl + "/" + createdCourse.getId(), Course.class);
        assertEquals(HttpStatus.NOT_FOUND.value(), verifyResponse.getStatusCodeValue());
    }
}

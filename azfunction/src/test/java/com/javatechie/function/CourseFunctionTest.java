package com.javatechie.function;

import com.javatechie.dto.Course;
import com.javatechie.service.CourseService;
import com.microsoft.azure.functions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CourseFunctionTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseFunction courseFunction;

    @Mock
    private HttpRequestMessage<Course> requestWithBody;

    @Mock
    private HttpRequestMessage<Void> request;

    @Mock
    private ExecutionContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCourses_ShouldReturnOkWithCourses() {
        // Arrange
        Course course1 = new Course(1, "Java", 99.99);
        Course course2 = new Course(2, "Python", 89.99);
        when(courseService.getAllCourses()).thenReturn(Arrays.asList(course1, course2));

        HttpResponseMessage.Builder builder = mock(HttpResponseMessage.Builder.class);
        HttpResponseMessage responseMock = mock(HttpResponseMessage.class);
        when(request.createResponseBuilder(any(HttpStatus.class))).thenReturn(builder);
        when(builder.body(any())).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(responseMock);

        // Act
        HttpResponseMessage response = courseFunction.getAllCourses(request, context);

        // Assert
        verify(courseService).getAllCourses();
        verify(request).createResponseBuilder(HttpStatus.OK);
    }

    @Test
    void getCourseById_WhenExists_ShouldReturnOkWithCourse() {
        // Arrange
        Course course = new Course(1, "Java", 99.99);
        when(courseService.getCourseById(1)).thenReturn(Optional.of(course));

        HttpResponseMessage.Builder builder = mock(HttpResponseMessage.Builder.class);
        HttpResponseMessage responseMock = mock(HttpResponseMessage.class);
        when(request.createResponseBuilder(any(HttpStatus.class))).thenReturn(builder);
        when(builder.body(any())).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(responseMock);

        // Act
        HttpResponseMessage response = courseFunction.getCourseById(request, 1, context);

        // Assert
        verify(courseService).getCourseById(1);
        verify(request).createResponseBuilder(HttpStatus.OK);
    }

    @Test
    void createCourse_ShouldReturnCreatedWithCourse() {
        // Arrange
        Course course = new Course(1, "Java", 99.99);
        when(courseService.addCourse(any(Course.class))).thenReturn(course);
        when(requestWithBody.getBody()).thenReturn(course);

        HttpResponseMessage.Builder builder = mock(HttpResponseMessage.Builder.class);
        HttpResponseMessage responseMock = mock(HttpResponseMessage.class);
        when(requestWithBody.createResponseBuilder(any(HttpStatus.class))).thenReturn(builder);
        when(builder.body(any())).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(responseMock);

        // Act
        HttpResponseMessage response = courseFunction.createCourse(requestWithBody, context);

        // Assert
        verify(courseService).addCourse(any(Course.class));
        verify(requestWithBody).createResponseBuilder(HttpStatus.CREATED);
    }

    @Test
    void updateCourse_WhenExists_ShouldReturnOkWithUpdatedCourse() {
        // Arrange
        Course course = new Course(1, "Updated Java", 149.99);
        when(courseService.updateCourse(anyInt(), any(Course.class))).thenReturn(Optional.of(course));
        when(requestWithBody.getBody()).thenReturn(course);

        HttpResponseMessage.Builder builder = mock(HttpResponseMessage.Builder.class);
        HttpResponseMessage responseMock = mock(HttpResponseMessage.class);
        when(requestWithBody.createResponseBuilder(any(HttpStatus.class))).thenReturn(builder);
        when(builder.body(any())).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(responseMock);

        // Act
        HttpResponseMessage response = courseFunction.updateCourse(requestWithBody, 1, context);

        // Assert
        verify(courseService).updateCourse(eq(1), any(Course.class));
        verify(requestWithBody).createResponseBuilder(HttpStatus.OK);
    }

    @Test
    void deleteCourse_WhenExists_ShouldReturnNoContent() {
        // Arrange
        when(courseService.deleteCourse(1)).thenReturn(true);

        HttpResponseMessage.Builder builder = mock(HttpResponseMessage.Builder.class);
        HttpResponseMessage responseMock = mock(HttpResponseMessage.class);
        when(request.createResponseBuilder(any(HttpStatus.class))).thenReturn(builder);
        when(builder.build()).thenReturn(responseMock);

        // Act
        HttpResponseMessage response = courseFunction.deleteCourse(request, 1, context);

        // Assert
        verify(courseService).deleteCourse(1);
        verify(request).createResponseBuilder(HttpStatus.NO_CONTENT);
    }
}

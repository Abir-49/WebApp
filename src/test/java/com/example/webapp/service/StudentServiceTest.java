package com.example.webapp.service;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Student;
import com.example.webapp.repository.CourseRepository;
import com.example.webapp.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository; // Mock CourseRepository

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        course1 = new Course("Math", 3, null); // Department will be set by DataLoader or during actual save
        course1.setId(100L);
        course2 = new Course("Physics", 4, null);
        course2.setId(101L);

        student1 = new Student();
        student1.setId(1L);
        student1.setName("Rahim");
        student1.setRoll("CSE-01");
        student1.setCourses(new HashSet<>(Set.of(course1)));
    }

    @Test
    void getAllStudents_shouldReturnStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student1));

        List<Student> students = studentService.getAllStudents();

        assertEquals(1, students.size());
        assertEquals("Rahim", students.get(0).getName());
        verify(studentRepository).findAll();
    }

    @Test
    void getStudentById_shouldReturnStudent() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));

        Optional<Student> result = studentService.getStudentById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Rahim", result.get().getName());
        verify(studentRepository).findById(1L);
    }

    @Test
    void saveStudent_shouldSaveStudentWithCourses() {
        Student newStudent = new Student("Karim", "CSE-02", new HashSet<>(Set.of(course2)));
        when(courseRepository.findById(course2.getId())).thenReturn(Optional.of(course2));
        when(studentRepository.save(any(Student.class))).thenReturn(newStudent);

        Student saved = studentService.saveStudent(newStudent);

        assertNotNull(saved);
        assertEquals("Karim", saved.getName());
        assertFalse(saved.getCourses().isEmpty());
        verify(courseRepository).findById(course2.getId());
        verify(studentRepository).save(newStudent);
    }

    @Test
    void updateStudent_shouldUpdateStudentAndCourses() {
        Student updatedDetails = new Student("Rahim Updated", "CSE-01-UP", new HashSet<>(Set.of(course2)));
        updatedDetails.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(courseRepository.findById(course2.getId())).thenReturn(Optional.of(course2));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedDetails);

        Student updated = studentService.updateStudent(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Rahim Updated", updated.getName());
        assertEquals("CSE-01-UP", updated.getRoll());
        assertEquals(1, updated.getCourses().size());
        assertTrue(updated.getCourses().contains(course2));
        verify(studentRepository).findById(1L);
        verify(courseRepository).findById(course2.getId());
        verify(studentRepository).save(student1); // student1 is the one being modified and saved
    }

    @Test
    void deleteStudent_shouldDeleteStudent() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        doNothing().when(studentRepository).delete(student1);

        studentService.deleteStudent(1L);

        verify(studentRepository).findById(1L);
        verify(studentRepository).delete(student1);
    }

    @Test
    void getStudentById_shouldReturnEmptyOptionalForNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Student> result = studentService.getStudentById(99L);

        assertFalse(result.isPresent());
        verify(studentRepository).findById(99L);
    }

    @Test
    void createStudent_shouldThrowExceptionIfCourseNotFound() {
        Student newStudent = new Student("Karim", "CSE-02", new HashSet<>(Set.of(new Course("NonExistent", 3, null))));
        newStudent.getCourses().iterator().next().setId(999L); // Set a non-existent ID

        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            studentService.saveStudent(newStudent);
        });

        assertTrue(thrown.getMessage().contains("Course not found with ID: 999"));
        verify(courseRepository).findById(999L);
        verify(studentRepository, never()).save(any(Student.class));
    }
}

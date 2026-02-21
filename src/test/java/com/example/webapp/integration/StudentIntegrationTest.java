package com.example.webapp.integration;

import com.example.webapp.entity.Student;
import com.example.webapp.repository.StudentRepository;
import com.example.webapp.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // This ensures each test starts with a clean slate by rolling back
class StudentIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        // Optional: Manual cleanup if not using @Transactional
        // studentRepository.deleteAll();
    }

    @Test
    void testSaveAndRetrieveStudentFlow() {
        // 1. Create Student entity
        Student student = new Student();
        student.setName("Integration Test Student");
        student.setRoll("INT-001");
        student.setCourses(new HashSet<>()); // Initialize empty set

        // 2. Act: Save through Service
        Student savedStudent = studentService.saveStudent(student);

        // 3. Assert: Verify saved object
        assertNotNull(savedStudent.getId());
        assertEquals("Integration Test Student", savedStudent.getName());

        // 4. Verify: Retrieve from DB using a different Service method
        Optional<Student> fetched = studentService.getStudentById(savedStudent.getId());

        assertTrue(fetched.isPresent());
        assertEquals("INT-001", fetched.get().getRoll());
    }

    @Test
    void testGetAllStudentsFlow() {
        // Arrange: Directly save a few entities
        Student s1 = new Student();
        s1.setName("User 1");
        s1.setRoll("R1");
        s1.setCourses(new HashSet<>());
        studentRepository.save(s1);

        Student s2 = new Student();
        s2.setName("User 2");
        s2.setRoll("R2");
        s2.setCourses(new HashSet<>());
        studentRepository.save(s2);

        // Act: Use Service to fetch all
        List<Student> allStudents = studentService.getAllStudents();

        // Assert: Ensure the service sees the real DB data
        assertEquals(2, allStudents.size()); // Expect exactly 2 if @Transactional is working
    }
}
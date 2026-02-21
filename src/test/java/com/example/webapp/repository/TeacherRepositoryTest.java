package com.example.webapp.repository;

import com.example.webapp.entity.Department;
import com.example.webapp.entity.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department("Physics");
        entityManager.persist(department);
        entityManager.flush();
    }

    @Test
    void findByEmail_shouldReturnTeacher() {
        Teacher teacher = new Teacher("Jane", "Doe", "jane.doe@example.com", department);
        entityManager.persist(teacher);
        entityManager.flush();

        Optional<Teacher> found = teacherRepository.findByEmail("jane.doe@example.com");

        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
    }

    @Test
    void findByDepartmentId_shouldReturnTeachers() {
        Teacher teacher1 = new Teacher("Jane", "Doe", "jane@example.com", department);
        Teacher teacher2 = new Teacher("John", "Smith", "john@example.com", department);
        entityManager.persist(teacher1);
        entityManager.persist(teacher2);
        entityManager.flush();

        List<Teacher> teachers = teacherRepository.findByDepartmentId(department.getId());

        assertEquals(2, teachers.size());
    }

    @Test
    void deleteTeacher_shouldRemoveFromDb() {
        Teacher teacher = new Teacher("Jane", "Doe", "jane.doe@example.com", department);
        entityManager.persist(teacher);
        entityManager.flush();

        teacherRepository.delete(teacher);
        entityManager.flush();

        Optional<Teacher> found = teacherRepository.findById(teacher.getId());
        assertFalse(found.isPresent());
    }
}

package com.example.webapp.service;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Department;
import com.example.webapp.entity.Teacher;
import com.example.webapp.repository.CourseRepository;
import com.example.webapp.repository.DepartmentRepository;
import com.example.webapp.repository.TeacherRepository;
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
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;
    private Department department;
    private Course course;

    @BeforeEach
    void setUp() {
        department = new Department("CS");
        department.setId(1L);

        course = new Course("Java", 3, department);
        course.setId(1L);

        teacher = new Teacher("John", "Doe", "john.doe@example.com", department);
        teacher.setId(1L);
        teacher.setCourses(new HashSet<>(Set.of(course)));
    }

    @Test
    void getAllTeachers_shouldReturnTeachers() {
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));

        List<Teacher> result = teacherService.getAllTeachers();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        verify(teacherRepository).findAll();
    }

    @Test
    void createTeacher_shouldSaveTeacher() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        Teacher saved = teacherService.createTeacher(teacher);

        assertNotNull(saved);
        assertEquals("John", saved.getFirstName());
        verify(teacherRepository).save(teacher);
    }

    @Test
    void createTeacher_shouldThrowExceptionIfDepartmentNotFound() {
        teacher.getDepartment().setId(99L);
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> teacherService.createTeacher(teacher));
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    void deleteTeacher_shouldDelete() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        doNothing().when(teacherRepository).delete(teacher);

        teacherService.deleteTeacher(1L);

        verify(teacherRepository).delete(teacher);
    }
}

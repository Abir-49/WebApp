package com.example.webapp.service;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Department;
import com.example.webapp.entity.Teacher;
import com.example.webapp.repository.CourseRepository;
import com.example.webapp.repository.DepartmentRepository;
import com.example.webapp.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher createTeacher(Teacher teacher) {
        // Ensure department exists
        if (teacher.getDepartment() == null || teacher.getDepartment().getId() == null) {
            throw new IllegalArgumentException("Teacher must be associated with an existing Department.");
        }
        Department department = departmentRepository.findById(teacher.getDepartment().getId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + teacher.getDepartment().getId()));
        teacher.setDepartment(department);

        // Handle courses if provided (e.g., from a form with course IDs)
        Set<Course> managedCourses = teacher.getCourses().stream()
                .map(course -> courseRepository.findById(course.getId())
                        .orElseThrow(() -> new RuntimeException("Course not found with ID: " + course.getId())))
                .collect(Collectors.toSet());
        teacher.setCourses(managedCourses);

        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found for this id :: " + id));

        teacher.setFirstName(teacherDetails.getFirstName());
        teacher.setLastName(teacherDetails.getLastName());
        teacher.setEmail(teacherDetails.getEmail());

        // Update department if changed
        if (teacherDetails.getDepartment() != null && teacherDetails.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(teacherDetails.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + teacherDetails.getDepartment().getId()));
            teacher.setDepartment(department);
        } else {
            throw new IllegalArgumentException("Teacher must be associated with an existing Department.");
        }

        // Update courses
        Set<Course> managedCourses = teacherDetails.getCourses().stream()
                .map(course -> courseRepository.findById(course.getId())
                        .orElseThrow(() -> new RuntimeException("Course not found with ID: " + course.getId())))
                .collect(Collectors.toSet());
        teacher.setCourses(managedCourses);


        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found for this id :: " + id));
        teacherRepository.delete(teacher);
    }

    public Optional<Teacher> getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }
}

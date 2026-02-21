package com.example.webapp.service;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Department;
import com.example.webapp.repository.CourseRepository;
import com.example.webapp.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course course) {
        // Ensure the department exists before saving the course
        if (course.getDepartment() != null && course.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(course.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + course.getDepartment().getId()));
            course.setDepartment(department);
        } else {
            throw new IllegalArgumentException("Course must be associated with an existing Department.");
        }
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found for this id :: " + id));
        course.setName(courseDetails.getName());
        course.setCredits(courseDetails.getCredits());

        if (courseDetails.getDepartment() != null && courseDetails.getDepartment().getId() != null) {
            Department department = departmentRepository.findById(courseDetails.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found with ID: " + courseDetails.getDepartment().getId()));
            course.setDepartment(department);
        } else {
            throw new IllegalArgumentException("Course must be associated with an existing Department.");
        }

        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found for this id :: " + id));
        courseRepository.delete(course);
    }

    public Optional<Course> getCourseByName(String name) {
        return courseRepository.findByName(name);
    }
}

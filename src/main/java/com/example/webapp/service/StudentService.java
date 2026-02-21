package com.example.webapp.service;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Student;
import com.example.webapp.repository.CourseRepository;
import com.example.webapp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student student) {
        if (student.getCourses() != null && !student.getCourses().isEmpty()) {
            Set<Course> managedCourses = student.getCourses().stream()
                    .map(course -> courseRepository.findById(course.getId())
                            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + course.getId())))
                    .collect(Collectors.toSet());
            student.setCourses(managedCourses);
        } else {
            student.setCourses(new HashSet<>());
        }
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found for this id :: " + id));

        student.setName(studentDetails.getName());
        student.setRoll(studentDetails.getRoll());

        if (studentDetails.getCourses() != null && !studentDetails.getCourses().isEmpty()) {
            Set<Course> managedCourses = studentDetails.getCourses().stream()
                    .map(course -> courseRepository.findById(course.getId())
                            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + course.getId())))
                    .collect(Collectors.toSet());
            student.setCourses(managedCourses);
        } else {
            student.setCourses(new HashSet<>());
        }

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found for this id :: " + id));
        studentRepository.delete(student);
    }

    public Optional<Student> getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }
}

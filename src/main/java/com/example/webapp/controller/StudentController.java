package com.example.webapp.controller;

import com.example.webapp.dto.StudentDTO; // Import StudentDTO
import com.example.webapp.entity.Course;
import com.example.webapp.entity.Student;
import com.example.webapp.entity.User;
import com.example.webapp.service.CourseService;
import com.example.webapp.service.StudentService;
import com.example.webapp.repository.UserRepository; // Import UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository; // Inject UserRepository

    @GetMapping
    public String getAllStudents(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        // Check if the current user has the ROLE_STUDENT
        boolean isStudent = authentication.getAuthorities().stream()
                                .anyMatch(r -> r.getAuthority().equals("ROLE_STUDENT"));

        if (isStudent) {
            // If it's a student, show only their own student profile
            User currentUser = userRepository.findByUsername(currentUsername)
                                    .orElseThrow(() -> new RuntimeException("Logged-in user not found!"));
            Student student = studentService.getStudentByUserId(currentUser.getId())
                                .orElseThrow(() -> new RuntimeException("Student profile not found for user: " + currentUsername));
            model.addAttribute("students", Collections.singletonList(student));
        } else {
            // For TEACHERs and ADMINs, show all students
            model.addAttribute("students", studentService.getAllStudents());
        }
        return "students";
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        // Only ADMIN or TEACHER can access this form, and a student can edit their own profile
        boolean isAdminOrTeacher = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_TEACHER"));
        boolean isStudent = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_STUDENT"));

        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        if (isStudent && !student.getUser().getUsername().equals(authentication.getName())) {
            return "redirect:/students"; // Student trying to edit another student's profile
        } else if (!isAdminOrTeacher && !isStudent) {
             return "redirect:/students"; // Not authorized role
        }

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId()); // Populate ID for editing
        studentDTO.setName(student.getName());
        studentDTO.setRoll(student.getRoll());
        studentDTO.setSelectedCourseIds(student.getCourses().stream()
                .map(Course::getId).collect(Collectors.toSet()));

        model.addAttribute("studentDTO", studentDTO);
        model.addAttribute("allCourses", courseService.getAllCourses());
        return "student-form";
    }

    @PostMapping("/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute StudentDTO studentDTO,
                                Authentication authentication) {
        // Only ADMIN or TEACHER can update students, and a student can update their own profile
        boolean isAdminOrTeacher = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_TEACHER"));
        boolean isStudent = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_STUDENT"));
        
        // Fetch the existing student to check ownership if current user is a student
        Student existingStudent = studentService.getStudentById(id)
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        if (isStudent && (existingStudent.getUser() == null || !existingStudent.getUser().getUsername().equals(authentication.getName()))) {
            return "redirect:/students"; // Student trying to update another student's profile
        } else if (!isAdminOrTeacher && !isStudent) {
            return "redirect:/students"; // Not authorized role
        }

        existingStudent.setName(studentDTO.getName());
        existingStudent.setRoll(studentDTO.getRoll());

        if (studentDTO.getSelectedCourseIds() != null && !studentDTO.getSelectedCourseIds().isEmpty()) {
            Set<Course> courses = studentDTO.getSelectedCourseIds().stream()
                    .map(courseId -> courseService.getCourseById(courseId).orElseThrow(() -> new RuntimeException("Course not found")))
                    .collect(Collectors.toSet());
            existingStudent.setCourses(courses);
        } else {
            existingStudent.setCourses(new HashSet<>());
        }
        studentService.updateStudent(id, existingStudent);
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, Authentication authentication) {
        // Only ADMIN or TEACHER can delete students
        if (!authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_TEACHER"))) {
            return "redirect:/students"; // Redirect if not authorized
        }
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
}

package com.example.webapp.service;

import com.example.webapp.dto.UserDTO;
import com.example.webapp.entity.Role;
import com.example.webapp.entity.User;
import com.example.webapp.entity.Student;
import com.example.webapp.entity.Teacher;
import com.example.webapp.entity.Department;
import com.example.webapp.entity.Course; // Added
import com.example.webapp.repository.RoleRepository;
import com.example.webapp.repository.UserRepository;
import com.example.webapp.repository.StudentRepository;
import com.example.webapp.repository.TeacherRepository;
import com.example.webapp.repository.DepartmentRepository;
import com.example.webapp.repository.CourseRepository; // Added
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors; // Added

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository; // Added

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       StudentRepository studentRepository, TeacherRepository teacherRepository,
                       DepartmentRepository departmentRepository, CourseRepository courseRepository) { // Updated constructor
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public User saveUser(UserDTO userDTO) {
        // Check if user already exists
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username " + userDTO.getUsername() + " already exists.");
        }

        // Fetch the role
        Role role = roleRepository.findById(userDTO.getRoleId())
                                  .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + userDTO.getRoleId()));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        // Encode the password
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        // Create and save the new user
        User newUser = new User(userDTO.getUsername(), encodedPassword, roles);
        User savedUser = userRepository.save(newUser); // Save user first to get ID

        // Create and link Student or Teacher profile based on role
        if (role.getName().equals("ROLE_STUDENT")) {
            // Use details from UserDTO
            Set<Course> studentCourses = new HashSet<>();
            if (userDTO.getStudentCourseIds() != null && !userDTO.getStudentCourseIds().isEmpty()) {
                studentCourses = userDTO.getStudentCourseIds().stream()
                        .map(id -> courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found with ID: " + id)))
                        .collect(Collectors.toSet());
            }

            Student newStudent = new Student(userDTO.getStudentName(), userDTO.getStudentRoll(), studentCourses, savedUser);
            savedUser.setStudent(studentRepository.save(newStudent));
        } else if (role.getName().equals("ROLE_TEACHER")) {
            // Use details from UserDTO
            Department teacherDepartment = null;
            if (userDTO.getTeacherDepartmentId() != null) {
                teacherDepartment = departmentRepository.findById(userDTO.getTeacherDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found with ID: " + userDTO.getTeacherDepartmentId()));
            } else {
                throw new IllegalArgumentException("Teacher must be associated with a Department.");
            }

            Set<Course> teacherCourses = new HashSet<>();
            if (userDTO.getTeacherCourseIds() != null && !userDTO.getTeacherCourseIds().isEmpty()) {
                teacherCourses = userDTO.getTeacherCourseIds().stream()
                        .map(id -> courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found with ID: " + id)))
                        .collect(Collectors.toSet());
            }

            Teacher newTeacher = new Teacher(userDTO.getTeacherFirstName(), userDTO.getTeacherLastName(), userDTO.getTeacherEmail(), teacherDepartment, savedUser);
            newTeacher.setCourses(teacherCourses); // Set courses for teacher
            savedUser.setTeacher(teacherRepository.save(newTeacher));
        }

        // Re-save the user to update the student/teacher link (if not cascade all)
        return userRepository.save(savedUser);
    }
}

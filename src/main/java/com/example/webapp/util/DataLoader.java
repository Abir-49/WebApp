package com.example.webapp.util;

import com.example.webapp.entity.*;
import com.example.webapp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                      DepartmentRepository departmentRepository, CourseRepository courseRepository,
                      TeacherRepository teacherRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional // Ensure all operations are part of a single transaction
    public void run(String... args) throws Exception {
        // Create Roles
        Role studentRole = roleRepository.findByName("ROLE_STUDENT").orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT")));
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER").orElseGet(() -> roleRepository.save(new Role("ROLE_TEACHER")));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // Create Users (if not exists)
        User adminUser = userRepository.findByUsername("admin").orElseGet(() -> {
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(teacherRole); // Admin can also have teacher capabilities
            return userRepository.save(new User("admin", passwordEncoder.encode("password"), roles));
        });

        User teacherUser = userRepository.findByUsername("teacher").orElseGet(() -> {
            Set<Role> roles = new HashSet<>();
            roles.add(teacherRole);
            return userRepository.save(new User("teacher", passwordEncoder.encode("password"), roles));
        });

        User studentUser = userRepository.findByUsername("student").orElseGet(() -> {
            Set<Role> roles = new HashSet<>();
            roles.add(studentRole);
            return userRepository.save(new User("student", passwordEncoder.encode("password"), roles));
        });

        // Create Departments (if not exists)
        Department compSci = departmentRepository.findByName("Computer Science").orElseGet(() -> departmentRepository.save(new Department("Computer Science")));
        Department physics = departmentRepository.findByName("Physics").orElseGet(() -> departmentRepository.save(new Department("Physics")));

        // Create Courses (if not exists)
        Course algos = courseRepository.findByName("Algorithms").orElseGet(() -> courseRepository.save(new Course("Algorithms", 3, compSci)));
        Course oop = courseRepository.findByName("Object-Oriented Programming").orElseGet(() -> courseRepository.save(new Course("Object-Oriented Programming", 4, compSci)));
        Course mechanics = courseRepository.findByName("Classical Mechanics").orElseGet(() -> courseRepository.save(new Course("Classical Mechanics", 3, physics)));

        // Create Teachers and link to Users (if not exists)
        if (teacherRepository.findByEmail("john.doe@example.com").isEmpty()) {
            Teacher john = new Teacher("John", "Doe", "john.doe@example.com", compSci, adminUser); // Link adminUser
            john.getCourses().add(algos);
            john.getCourses().add(oop);
            teacherRepository.save(john);
            adminUser.setTeacher(john); // Set bidirectional link
            userRepository.save(adminUser);
        }

        if (teacherRepository.findByEmail("jane.smith@example.com").isEmpty()) {
            Teacher jane = new Teacher("Jane", "Smith", "jane.smith@example.com", physics, teacherUser); // Link teacherUser
            jane.getCourses().add(mechanics);
            teacherRepository.save(jane);
            teacherUser.setTeacher(jane); // Set bidirectional link
            userRepository.save(teacherUser);
        }

        // Create Students and link to Users (if not exists)
        if (studentRepository.findByRoll("STU001").isEmpty()) {
            Student alice = new Student("Alice", "STU001", new HashSet<Course>(), studentUser); // Link studentUser
            alice.getCourses().add(algos);
            studentRepository.save(alice);
            studentUser.setStudent(alice); // Set bidirectional link
            userRepository.save(studentUser);
        }

        // Add another student without a direct user for testing
        if (studentRepository.findByRoll("STU002").isEmpty()) {
            Student bob = new Student("Bob", "STU002", new HashSet<Course>()); // No user linked
            bob.getCourses().add(oop);
            bob.getCourses().add(mechanics);
            studentRepository.save(bob);
        }
    }
}

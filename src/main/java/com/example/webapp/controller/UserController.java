package com.example.webapp.controller;

import com.example.webapp.dto.UserDTO;
import com.example.webapp.entity.Role;
import com.example.webapp.service.CourseService; // Added
import com.example.webapp.service.DepartmentService; // Added
import com.example.webapp.service.RoleService;
import com.example.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final CourseService courseService; // Added
    private final DepartmentService departmentService; // Added

    @Autowired
    public UserController(UserService userService, RoleService roleService,
                          CourseService courseService, DepartmentService departmentService) { // Updated constructor
        this.userService = userService;
        this.roleService = roleService;
        this.courseService = courseService;
        this.departmentService = departmentService;
    }

    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("allCourses", courseService.getAllCourses()); // Added
        model.addAttribute("allDepartments", departmentService.getAllDepartments()); // Added
        return "user-form"; // This will be the Thymeleaf template name
    }

    @PostMapping
    public String createUser(@ModelAttribute UserDTO userDTO) {
        userService.saveUser(userDTO);
        return "redirect:/"; // Redirect to homepage after user creation
    }
}

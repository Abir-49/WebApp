package com.example.webapp.controller;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Department;
import com.example.webapp.entity.Teacher;
import com.example.webapp.service.CourseService;
import com.example.webapp.service.DepartmentService;
import com.example.webapp.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String getAllTeachers(Model model) {
        List<Teacher> teachers = teacherService.getAllTeachers();
        model.addAttribute("teachers", teachers);
        return "teachers"; // Assuming you have a teachers.html Thymeleaf template
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.getTeacherById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid teacher Id:" + id));
        model.addAttribute("teacher", teacher);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("allCourses", courseService.getAllCourses());
        model.addAttribute("selectedCourseIds", teacher.getCourses().stream()
                .map(Course::getId).collect(Collectors.toList()));
        return "teacher-form";
    }

    @PostMapping("/{id}")
    public String updateTeacher(@PathVariable Long id, @ModelAttribute Teacher teacher,
                                @RequestParam(value = "selectedCourseIds", required = false) List<Long> selectedCourseIds) {
        if (selectedCourseIds != null && !selectedCourseIds.isEmpty()) {
            Set<Course> courses = selectedCourseIds.stream()
                    .map(courseId -> courseService.getCourseById(courseId).orElseThrow(() -> new RuntimeException("Course not found")))
                    .collect(Collectors.toSet());
            teacher.setCourses(courses);
        } else {
            teacher.setCourses(new HashSet<>());
        }
        teacherService.updateTeacher(id, teacher);
        return "redirect:/teachers";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return "redirect:/teachers";
    }
}

package com.example.webapp.controller;

import com.example.webapp.entity.Course;
import com.example.webapp.entity.Department;
import com.example.webapp.service.CourseService;
import com.example.webapp.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public String getAllCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "courses"; // Assuming you have a courses.html Thymeleaf template
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "course-form"; // Assuming you have a course-form.html Thymeleaf template
    }

    @PostMapping
    public String createCourse(@ModelAttribute Course course) {
        courseService.createCourse(course);
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        model.addAttribute("course", course);
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "course-form";
    }

    @PostMapping("/{id}")
    public String updateCourse(@PathVariable Long id, @ModelAttribute Course course) {
        courseService.updateCourse(id, course);
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }
}

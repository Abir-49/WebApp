package com.example.webapp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Data
public class UserDTO {
    private String username;
    private String password;
    private Long roleId; // To select a single role for the user

    // Student specific fields
    private String studentName;
    private String studentRoll;
    private Set<Long> studentCourseIds; // For initial course enrollment if applicable

    // Teacher specific fields
    private String teacherFirstName;
    private String teacherLastName;
    private String teacherEmail;
    private Long teacherDepartmentId;
    private Set<Long> teacherCourseIds; // For initial course assignment if applicable
}
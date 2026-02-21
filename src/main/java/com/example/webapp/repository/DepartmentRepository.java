package com.example.webapp.repository;

import com.example.webapp.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // Custom query methods can be added here if needed
    Optional<Department> findByName(String name);
}

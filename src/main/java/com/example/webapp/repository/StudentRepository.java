package com.example.webapp.repository;

import com.example.webapp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findById(Long id);
    List<Student> findAll();
    Student save(Student student);
    void deleteById(Long id);
    Optional<Student> findByRoll(String roll);
    Optional<Student> findByUserId(Long userId); // Added this method
}

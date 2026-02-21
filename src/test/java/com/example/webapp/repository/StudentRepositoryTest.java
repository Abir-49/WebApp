package com.example.webapp.repository;

import com.example.webapp.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void testFindById(){
        Student student = new Student();
        student.setName("ABC");
        student.setRoll("1");

        this.studentRepository.save(student);

        Optional<Student> studentOptional = studentRepository.findById(student.getId());
        assertTrue(studentOptional.isPresent());
        assertEquals(student.getId(), studentOptional.get().getId());
    }

    @Test
    void testFindAll() {
        Student student1 = new Student();
        student1.setName("ABC");
        student1.setRoll("1");
        this.studentRepository.save(student1);

        Student student2 = new Student();
        student2.setName("DEF");
        student2.setRoll("2");
        this.studentRepository.save(student2);

        List<Student> students = this.studentRepository.findAll();
        assertFalse(students.isEmpty());
    }

}
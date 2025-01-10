package com.controller;

import com.entity.StudentEntity;
import com.repository.StudentRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private/admin")
public class StudentController {

    @Autowired
    StudentRepository studentRepo;

    @PostMapping("/addstudent")
    public ResponseEntity<?> addStudent(@RequestBody StudentEntity entity) {
        studentRepo.save(entity);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/getStudentByName")
    public ResponseEntity<?> getStudentByName(@RequestParam String characters) {
        if (characters == null || characters.length() < 1) {
            return ResponseEntity.ok("No Student Found");
        }
        List<StudentEntity> students = studentRepo.findByNameStartingWith(characters);
        return ResponseEntity.ok(students); 
    }

    @GetMapping("/getStudentById/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Integer id){
        Optional<StudentEntity> op = studentRepo.findById(id);
        if(op.isPresent()){
            return ResponseEntity.ok(op.get());
        }
        return ResponseEntity.ok("Student not found");
    }

    @PutMapping("/editstudent/{id}")
    public ResponseEntity<?> editStudent(@PathVariable Integer id, @RequestBody StudentEntity entity){
        Optional<StudentEntity> op = studentRepo.findById(id);
        HashMap<String,Object> response = new HashMap<String,Object>();
        if(op.isPresent()){
            StudentEntity student = op.get();
            student.setName(entity.getName());
            student.setEmail(entity.getEmail());
            student.setMobile(entity.getMobile());
            student.setBatch(entity.getBatch());
            student.setCollege(entity.getCollege());
            student.setCommunication(entity.getCommunication());
            student.setDiscipline(entity.getDiscipline());
            student.setRegularity(entity.getRegularity());
            student.setTestPerformance(entity.getTestPerformance());
            studentRepo.save(student);
            response.put("message", "Student updated successfully");
            return ResponseEntity.ok(response);
        }
        response.put("error", "Student not found");
        return ResponseEntity.ok(response);
    }
}

package com.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Student")
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer studentId;
    String name;
    String email;
    String mobile;
    String batch;
    String college;
    String discipline;
    String regularity;
    String communication;
    String testPerformance;
}

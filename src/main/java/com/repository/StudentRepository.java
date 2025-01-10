package com.repository;

import com.entity.StudentEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<StudentEntity, Integer> {
  @Query("SELECT s FROM StudentEntity s WHERE LOWER(s.name) LIKE LOWER(CONCAT(:characters, '%'))")
  List<StudentEntity> findByNameStartingWith(@Param("characters") String characters);
}

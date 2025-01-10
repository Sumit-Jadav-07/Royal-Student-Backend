package com.repository;

import com.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {
  AdminEntity findByEmail(String email);
}

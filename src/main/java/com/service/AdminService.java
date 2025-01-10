package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.AdminEntity;
import com.repository.AdminRepository;

@Service
public class AdminService {

  @Autowired
  AdminRepository adminRepo;

  public AdminEntity authenticateAdmin(String email){
    AdminEntity admin = adminRepo.findByEmail(email);
    if(admin == null){
      return null;
    }
    return admin;
  }
  
}

package com.controller;

import com.bean.LoginRequest;
import com.entity.AdminEntity;
import com.repository.AdminRepository;
import com.service.AdminService;
import com.service.JWTService;
import com.service.OtpService;
import com.service.TokenBlackListService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/session")
public class SessionController {

    @Autowired
    AdminRepository adminRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    AdminService adminService;

    @Autowired
    JWTService jwtService;

    @Autowired
    OtpService otpService;

    @Autowired
    JavaMailSender sender;

    @Autowired
    private TokenBlackListService tokenBlackListService;

    @PostMapping("/signup")
    public ResponseEntity<?> Signup(@RequestBody AdminEntity entity) {
        AdminEntity admin = adminService.authenticateAdmin(entity.getEmail());
        HashMap<String, Object> response = new HashMap<String, Object>();
        if (admin == null) {
            entity.setPassword(encoder.encode(entity.getPassword()));
            adminRepo.save(entity);
            response.put("message", "Signup successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Email already exists");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody LoginRequest login) {
        AdminEntity admin = adminService.authenticateAdmin(login.getEmail());
        HashMap<String, Object> response = new HashMap<>();
        if (admin != null) {
            if (encoder.matches(login.getPassword(), admin.getPassword())) {
                String token = jwtService.generateToken(login.getEmail());
                response.put("message", "Successfully Login");
                response.put("token", token);
                return ResponseEntity.ok()
                        .header("Authorization", "Bearer " + token)
                        .body(response);
            } else {
                response.put("error", "Wrong password");
                return ResponseEntity.ok(response);
            }
        }
        response.put("error", "Wrong email");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            tokenBlackListService.blacklistToken(token);
            return ResponseEntity.ok("Logout successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No token found in request");
        }
    }

    @PostMapping("/sendotp")
    public ResponseEntity<?> sendOtp(@RequestBody LoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        Object admin = adminService.authenticateAdmin(email);
        HashMap<String, Object> response = new HashMap<>();
        if (admin == null) {
            response.put("error", "Email not found");
            return ResponseEntity.ok(response);
        }
        String otp = otpService.getOtp();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP");
        message.setText(otp);
        sender.send(message);
        session.setAttribute("otp", otp);
        System.out.println(otp);
        response.put("message", "OTP sent successfully");
        response.put("otp", otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<?> forgotPassword(@RequestBody LoginRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        String storedOtp = (String) session.getAttribute("otp");
        HashMap<String, Object> response = new HashMap<>();
        System.out.println("Stored Otp " + storedOtp);
        Object admin = adminService.authenticateAdmin(email);

        if (admin == null) {
            response.put("error", "Email not Found");
            return ResponseEntity.ok(response);
        }

        AdminEntity adminEntity = (AdminEntity) admin;
        adminEntity.setPassword(encoder.encode(password));
        adminRepo.save(adminEntity);
        response.put("message", "Password updated successfully");
        return ResponseEntity.ok(response);
    }

}

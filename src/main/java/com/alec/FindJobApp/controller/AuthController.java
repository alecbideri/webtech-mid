package com.alec.FindJobApp.controller;

import com.alec.FindJobApp.dto.RegisterRequest;
import com.alec.FindJobApp.model.User;
import com.alec.FindJobApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    User user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(request.getPassword())
        .name(request.getName())
        .role(request.getRole())
        .build();

    return ResponseEntity.ok(userService.registerUser(user));
  }

  @GetMapping("/test")
  public ResponseEntity<String> test() {
    return ResponseEntity.ok("Auth service is working!");
  }
}

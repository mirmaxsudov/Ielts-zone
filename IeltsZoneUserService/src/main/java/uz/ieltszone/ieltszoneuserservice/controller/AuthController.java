package uz.ieltszone.ieltszoneuserservice.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.LoginRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.RoleCheckRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.JwtResponse;
import uz.ieltszone.ieltszoneuserservice.service.base.AuthService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody LoginRequest request) {
        JwtResponse authenticate = authService.authenticate(request);
        return ResponseEntity.ok(authenticate);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.generateAccessToken(request));
    }

    @GetMapping("/check-roles")
    public ResponseEntity<Boolean> checkRoles(@RequestBody RoleCheckRequest request) {
        return ResponseEntity.ok(
                authService.checkRoles(request)
        );
    }
}
package uz.ieltszone.ieltszoneuserservice.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.ieltszoneuserservice.model.entity.User;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.LoginRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.RoleCheckRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.JwtResponse;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserDetailsDTO;
import uz.ieltszone.ieltszoneuserservice.service.base.AuthService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody LoginRequest request, HttpServletResponse response) {
        JwtResponse authenticate = authService.authenticate(request, response);
        return ResponseEntity.ok(authenticate);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.generateAccessToken(request));
    }

    @PostMapping("/check-roles")
    public ResponseEntity<Boolean> checkRoles(@RequestBody RoleCheckRequest request) {
        System.out.println("Inside check roles");
        System.out.println("request.getToken() = " + request.getToken());
        return ResponseEntity.ok(authService.checkRoles(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'MANEGER')")
    public UserDetailsDTO me(@AuthenticationPrincipal User user) {
        System.out.println("user = " + user);
        return authService.me(user);
    }
}
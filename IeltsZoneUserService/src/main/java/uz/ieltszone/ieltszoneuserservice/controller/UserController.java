package uz.ieltszone.ieltszoneuserservice.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.LoginRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequestUpdate;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.JwtResponse;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserResponse;
import uz.ieltszone.ieltszoneuserservice.payload.ApiResponse;
import uz.ieltszone.ieltszoneuserservice.service.base.AuthService;
import uz.ieltszone.ieltszoneuserservice.service.base.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/save")
    public ResponseEntity<ApiResponse<UserResponse>> save(@RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.ok(userService.save(userRequest));
    }

    @PutMapping("/update/{userId}")
    public ApiResponse<UserResponse> update(@RequestBody UserRequestUpdate userRequest, @PathVariable("userId") Long userId) {
        return userService.update(userRequest, userId);
    }

    @GetMapping("/exists/{userId}")
    public boolean existsById(@PathVariable("userId") Long userId) {
        System.out.println("exists");
        return userService.existsById(userId);
    }

    @GetMapping("/{userId}")
    public UserResponse getById(@PathVariable("userId") Long userId) {
        return userService.getById(userId);
    }

    @GetMapping("/email/{email}")
    public UserResponse getByEmail(@PathVariable("email") String email) {
        return userService.getByEmail(email);
    }

    @GetMapping("/phoneNumber/{phoneNumber}")
    public UserResponse getByPhoneNumber(@PathVariable("phoneNumber") String phoneNumber) {
        return userService.getByPhoneNumber(phoneNumber);
    }

    @GetMapping("/existsByEmail/{email}")
    public boolean existsByEmail(@PathVariable("email") String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/all")
    public Iterable<UserResponse> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/update-img/{userId}")
    public ApiResponse<UserResponse> updateAttachment(@PathVariable("userId") Long userId, @RequestParam("attachmentId") Long attachmentId) {
        return userService.updateAttachment(userId, attachmentId);
    }
}
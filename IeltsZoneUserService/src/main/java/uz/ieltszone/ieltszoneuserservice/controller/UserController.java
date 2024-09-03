package uz.ieltszone.ieltszoneuserservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.ieltszone.ieltszoneuserservice.model.entity.User;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.UserRequestUpdate;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserResponse;
import uz.ieltszone.ieltszoneuserservice.payload.ApiResponse;
import uz.ieltszone.ieltszoneuserservice.service.base.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> save(@RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.ok(userService.save(userRequest));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'MANEGER')")
    @PutMapping("/update")
    public ApiResponse<UserResponse> update(@RequestBody UserRequestUpdate userRequest, @AuthenticationPrincipal User user) {
        return userService.update(userRequest, user.getId());
    }

    @GetMapping("/exists")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean existsById(@AuthenticationPrincipal User user) {
        System.out.println("exists");
        return userService.existsById(user.getId());
    }

    @GetMapping("/get-by-id")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'MANEGER')")
    public UserResponse getById(@AuthenticationPrincipal User user) {
        return userService.getById(user.getId());
    }

    @GetMapping("/email")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getByEmail(@AuthenticationPrincipal User user) {
        return userService.getByEmail(user.getEmail());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/phoneNumber")
    public UserResponse getByPhoneNumber(@AuthenticationPrincipal User user) {
        return userService.getByPhoneNumber(user.getPhoneNumber());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/existsByEmail/{email}")
    public boolean existsByEmail(@PathVariable("email") String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/all-teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<UserResponse> getAllTeachers() {
        return userService.getAllTeachers();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANEGER', 'TEACHER')")
    @PatchMapping("/update-img/{userId}")
    public ApiResponse<UserResponse> updateAttachment(@AuthenticationPrincipal User user, @RequestParam("attachmentId") Long attachmentId) {
        return userService.updateAttachment(user.getId(), attachmentId);
    }
}
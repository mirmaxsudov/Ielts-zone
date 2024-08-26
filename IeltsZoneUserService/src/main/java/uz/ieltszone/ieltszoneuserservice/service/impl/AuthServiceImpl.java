package uz.ieltszone.ieltszoneuserservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uz.ieltszone.ieltszoneuserservice.config.security.service.CustomUserDetailsService;
import uz.ieltszone.ieltszoneuserservice.config.security.service.JwtService;
import uz.ieltszone.ieltszoneuserservice.exceptions.CustomNotFoundException;
import uz.ieltszone.ieltszoneuserservice.exceptions.InvalidTokenException;
import uz.ieltszone.ieltszoneuserservice.model.entity.User;
import uz.ieltszone.ieltszoneuserservice.model.entity.enums.UserRole;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.LoginRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.RoleCheckRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.AttachmentResponse;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.JwtResponse;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserDetailsDTO;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserResponse;
import uz.ieltszone.ieltszoneuserservice.repository.UserRepository;
import uz.ieltszone.ieltszoneuserservice.service.base.AuthService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public JwtResponse authenticate(LoginRequest request, HttpServletResponse response) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(),
                        request.getPassword()));

        Optional<User> userOptional = userRepository.findByEmail(request.getLogin());

        if (userOptional.isEmpty())
            throw new CustomNotFoundException("User not found");

        User user1 = userOptional.get();
        String accessToken;
        String refreshToken;

        accessToken = jwtService.generateAccessToken(user1);
        refreshToken = jwtService.generateRefreshToken(user1);

        CompletableFuture.runAsync(
                () -> setRefreshTokenCookie(refreshToken, response)
        );

        return new JwtResponse(accessToken, refreshToken, getUserResponseDto(user1));
    }

    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); // -> 30 days
        cookie.setDomain("localhost");

        response.addCookie(cookie);
    }


    public JwtResponse generateAccessToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Authorization");
        if (refreshToken == null || !refreshToken.startsWith("Bearer "))
            return null;

        refreshToken = refreshToken.substring(7);

        if (jwtService.isTokenExpired(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String newAccessToken = jwtService.generateAccessToken(userDetails);

            Optional<User> userByLogin = userRepository.findByEmail(username);

            if (userByLogin.isEmpty())
                throw new CustomNotFoundException("User not found");

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setAccessToken(newAccessToken);
            jwtResponse.setRefreshToken(refreshToken);
            jwtResponse.setUser(getUserResponseDto(userByLogin.get()));

            return jwtResponse;
        }
        throw new InvalidTokenException("Refresh token expired");
    }

    @Override
    public Boolean checkRoles(RoleCheckRequest request) {
        List<UserRole> roles = request.getRoles();
        String token = request.getToken();

        System.out.println("token = " + token);

        if (token == null || !token.startsWith("Bearer "))
            return false;

        token = token.substring(7);

        String email = jwtService.extractUsername(token);

        System.out.println("email = " + email);

        User user = (User) userDetailsService.loadUserByUsername(email);

        System.out.println("us = " + user);

        return roles.stream()
                .anyMatch(role -> role.equals(user.getRole()));
    }

    @Override
    public UserDetailsDTO me(User user) {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setId(user.getId());
        userDetailsDTO.setPhoneNumber(user.getPhoneNumber());
        userDetailsDTO.setEmail(user.getEmail());

        return userDetailsDTO;
    }


    private UserResponse getUserResponseDto(User user) {
        Long attachmentId = user.getAttachmentId();
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .joinedAt(user.getJoinedAt())
                .role(user.getRole())
                .attachmentResponse(
                        attachmentId == null ? null :
                                AttachmentResponse.builder()
                                        .attachmentId(attachmentId)
                                        .url("localhost:8082/api/v1/file/get/" + user.getAttachmentId())
                                        .build()
                ).build();
    }
}
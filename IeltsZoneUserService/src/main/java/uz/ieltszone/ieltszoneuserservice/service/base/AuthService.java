package uz.ieltszone.ieltszoneuserservice.service.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uz.ieltszone.ieltszoneuserservice.model.entity.User;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.LoginRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.RoleCheckRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.JwtResponse;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.UserDetailsDTO;

public interface AuthService {
    JwtResponse authenticate(LoginRequest request, HttpServletResponse response);

    JwtResponse generateAccessToken(HttpServletRequest request);

    Boolean checkRoles(RoleCheckRequest request);

    UserDetailsDTO me(User user);
}

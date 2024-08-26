package uz.ieltszone.ieltszoneuserservice.service.base;

import jakarta.servlet.http.HttpServletRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.LoginRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.request.RoleCheckRequest;
import uz.ieltszone.ieltszoneuserservice.model.entity.response.JwtResponse;

public interface AuthService {
    JwtResponse authenticate(LoginRequest request);

    JwtResponse generateAccessToken(HttpServletRequest request);

    Boolean checkRoles(RoleCheckRequest request);
}

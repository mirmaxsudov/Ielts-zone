package uz.ieltszone.ieltszonefileservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import uz.ieltszone.ieltszonefileservice.entity.request.RoleCheckRequest;
import uz.ieltszone.ieltszonefileservice.exceptions.InvalidTokenException;
import uz.ieltszone.ieltszonefileservice.service.feign.UserFeign;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleCheckService {
    private final UserFeign userFeign;
    private final HttpServletRequest request;

    public void checkRoles(String[] requiredRoles) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token not found");
        }

        token = token.substring(7);

        RoleCheckRequest roleCheckRequest = new RoleCheckRequest();
        roleCheckRequest.setToken(token);
        roleCheckRequest.setRoles(List.of(requiredRoles));

        Boolean checked = userFeign.checkRoles(roleCheckRequest);

        System.out.println("checked = " + checked);

        if (!checked)
            throw new InvalidTokenException("Invalid token");
    }
}

package uz.ieltszone.ieltszonefileservice.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uz.ieltszone.ieltszonefileservice.entity.request.RoleCheckRequest;
import uz.ieltszone.ieltszonefileservice.exceptions.InvalidTokenException;
import uz.ieltszone.ieltszonefileservice.service.feign.UserFeign;

import java.util.List;
import java.util.Objects;

@Aspect
@RequiredArgsConstructor
public class RoleCheckAspect {
    private final UserFeign userFeign;

    @Before("@annotation(checkRole)")
    public void checkRole(CheckRole checkRole) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith("Bearer "))
            throw new InvalidTokenException("Token not found");

        token = token.substring(7);
        String[] requiredRoles = checkRole.roles();

        RoleCheckRequest roleCheckRequest = new RoleCheckRequest();
        roleCheckRequest.setToken(token);
        roleCheckRequest.setRoles(List.of(requiredRoles));

        Boolean checked = userFeign.checkRoles(roleCheckRequest);

        if (!checked)
            throw new InvalidTokenException("You do not have access to this resource");
    }
}
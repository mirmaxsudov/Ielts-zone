package uz.ieltszone.ieltszonefileservice.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uz.ieltszone.ieltszonefileservice.entity.request.RoleCheckRequest;
import uz.ieltszone.ieltszonefileservice.exceptions.InvalidTokenException;
import uz.ieltszone.ieltszonefileservice.service.feign.UserFeign;

import java.util.List;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final UserFeign userFeign;

    @Before("@annotation(checkRole)")
    public void checkUserRole(CheckRole checkRole) {
        System.out.println("Inside checkUserRole Aspect");

        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest();

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("Token: " + token);

        if (token == null)
            throw new InvalidTokenException("Token not found");

        RoleCheckRequest roleCheckRequest = new RoleCheckRequest();
        roleCheckRequest.setToken(token.trim());
        roleCheckRequest.setRoles(List.of(checkRole.roles()));

        System.out.println("roleCheckRequest = " + roleCheckRequest);

        Boolean checked = userFeign.checkRoles(roleCheckRequest).getBody();

        System.out.println("checked = " + checked);

        if (Boolean.FALSE.equals(checked)) {
            throw new InvalidTokenException("User does not have the required role(s)");
        }
    }
}

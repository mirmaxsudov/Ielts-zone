package uz.ieltszone.zonelifeservice.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uz.ieltszone.zonelifeservice.entity.dto.request.RoleCheckRequest;
import uz.ieltszone.zonelifeservice.exceptions.InvalidTokenException;
import uz.ieltszone.zonelifeservice.service.feign.UserFeign;

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
        roleCheckRequest.setToken(token.substring(7));
        roleCheckRequest.setRoles(List.of(checkRole.roles()));

        System.out.println("roleCheckRequest = " + roleCheckRequest);

        Boolean checked = userFeign.checkRoles(roleCheckRequest);

        System.out.println("checked = " + checked);

        if (!checked) {
            throw new InvalidTokenException("User does not have the required role(s)");
        }
    }
}

//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.http.HttpHeaders;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import uz.ieltszone.zonelifeservice.entity.dto.request.RoleCheckRequest;
//import uz.ieltszone.zonelifeservice.exceptions.InvalidTokenException;
//import uz.ieltszone.zonelifeservice.service.feign.UserFeign;
//
//import java.util.List;
//import java.util.Objects;
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//public class RoleCheckAspect {
//
//    private final UserFeign userFeign;
//
//    @Before("@annotation(checkRole)")
//    public void checkUserRole(CheckRole checkRole) {
//        System.out.println("Inside checkUserRole Aspect");
//
//        HttpServletRequest request = ((ServletRequestAttributes)
//                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
//                .getRequest();
//
//        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
//        System.out.println("Token: " + token);
//
//        if (token == null)
//            throw new InvalidTokenException("Token not found");
//
//        RoleCheckRequest roleCheckRequest = new RoleCheckRequest();
//        roleCheckRequest.setToken(token);
//        roleCheckRequest.setRoles(List.of(checkRole.roles()));
//
//        System.out.println("roleCheckRequest = " + roleCheckRequest);
//
//        Boolean checked = userFeign.checkRoles(roleCheckRequest);
//
//        System.out.println("checked = " + checked);
//
//        if (!checked) {
//            throw new InvalidTokenException("User does not have the required role(s)");
//        }
//    }
//}
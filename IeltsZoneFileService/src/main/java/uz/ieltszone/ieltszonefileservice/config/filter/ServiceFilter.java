package uz.ieltszone.ieltszonefileservice.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class ServiceFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String password = request.getHeader("password");

        if (password == null || password.isBlank())
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        else if (!password.equals("120"))
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        filterChain.doFilter(request, response);
    }
}

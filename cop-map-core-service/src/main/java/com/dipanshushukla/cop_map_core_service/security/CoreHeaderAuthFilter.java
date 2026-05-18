package com.dipanshushukla.cop_map_core_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class CoreHeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String badgeNumber = request.getHeader("X-Badge-Number");
        String roleStr = request.getHeader("X-Role");
        String thanaId = request.getHeader("X-Thana-Id");

        if (badgeNumber != null && roleStr != null && thanaId != null) {
            // Create principal
            CopMapPrincipal principal = new CopMapPrincipal(badgeNumber, roleStr, thanaId);

            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + roleStr));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
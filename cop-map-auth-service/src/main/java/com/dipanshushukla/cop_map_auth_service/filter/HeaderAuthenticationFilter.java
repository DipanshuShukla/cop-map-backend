package com.dipanshushukla.cop_map_auth_service.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dipanshushukla.cop_map_auth_service.entity.User;
import com.dipanshushukla.cop_map_auth_service.model.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String badgeNumber = request.getHeader("X-Badge-Number");
        String roleStr = request.getHeader("X-Role");
        String thanaId = request.getHeader("X-Thana-Id");

        // If the headers exist, the Gateway has authenticated this request
        if (badgeNumber != null && roleStr != null) {

            // 1. Build a "dummy" user object with just the necessary info.
            User authenticatedUser = new User();
            authenticatedUser.setBadgeNumber(badgeNumber);
            authenticatedUser.setRole(Role.valueOf(roleStr));
            authenticatedUser.setThanaId(thanaId);

            // 2. Grant the authority. Spring expects "ROLE_" prefix for @PreAuthorize
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + roleStr));

            // 3. Create the auth token and drop it into the SecurityContext
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    authenticatedUser, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
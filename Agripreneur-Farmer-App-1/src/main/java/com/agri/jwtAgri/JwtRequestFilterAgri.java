package com.agri.jwtAgri;
/*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilterAgri extends OncePerRequestFilter {

    private final JwtUtilAgri jwtUtil;
    private final CustomUserDetailsServiceAgri userDetailsService;

    public JwtRequestFilterAgri(JwtUtilAgri jwtUtil, CustomUserDetailsServiceAgri userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, java.io.IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String mobileNumber = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                mobileNumber = jwtUtil.extractUsername(jwtToken);
                System.out.println("Extracted Username: " + mobileNumber);
            } catch (Exception e) {
                System.out.println("JWT Token is invalid or expired: " + e.getMessage());
            }
        }

        
        if (mobileNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(mobileNumber);

            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("JWT Token is valid. User authenticated: " + mobileNumber);
            } else {
                System.out.println("JWT Token validation failed.");
            }
        } else {
            System.out.println("Mobile number is null or user already authenticated.");
        }
        filterChain.doFilter(request, response);
    }
}
*/